(ns spotty-fm.main

  (:require [org.httpkit.client :as http]
            [clojure.string :as string]
            [clojure.data.json :as json]
            [spotty-fm.lastfm :as lastfm]
            [spotty-fm.spotify :as spotify]
            [spotty-fm.core :refer [config]]
            [org.httpkit.server :as server])

  (:import java.io.InputStreamReader)
  
  (:gen-class))


(defn lastfm-to-spotify [token simple-track]
  (spotify/search-track
   token (str (:artist simple-track) " " (:title simple-track))))

(defn lastfm-tracks-to-spotify-uris
  [lastfm-tracks spotify-auth-token]
  (->> lastfm-tracks
       (map #(lastfm-to-spotify spotify-auth-token %))
       (filter (complement nil?))
       (map #(str "spotify:track:" (:spotify-id %)))))

(defn lastfm-user-tag-to-spotify-uris
  [lastfm-api-key lastfm-user tag spotify-auth-token]
  (lastfm-tracks-to-spotify-uris (lastfm/user-tagged-tracks lastfm-api-key lastfm-user tag) spotify-auth-token))

(defn lastfm-user-loved-to-spotify-uris
  [lastfm-api-key lastfm-user spotify-auth-token]
  (lastfm-tracks-to-spotify-uris (lastfm/user-loved-tracks lastfm-api-key lastfm-user) spotify-auth-token))


(defn page
  "return a seq of the sequences created by taking n items at a time (unless there are less than that remaining, in which case the remainder is the last element"
  [n aseq]
  (if (empty? aseq)
    '()
    (cons (take n aseq) (page n (drop n aseq)))))


(defn -main [arg & args]


  (let [{:keys [apikey]} (:lastfm config)
        {:keys [clientid secret authserver]} (:spotify config)]
    
    (println
     
     (json/write-str

      (case arg

        "lastfm-user-tag"
        (let [[user tag] args] (lastfm/user-tagged-tracks apikey user tag))
        
        "lastfm-user-loved"
        (let [user (first args)] (lastfm/user-loved-tracks apikey user))

        "spotify-search-tracks"
        (let [term (first args)
              token (spotify/fetch-client-auth-token clientid secret)]
          (spotify/search-tracks token term))

        "spotify-search-track"
        (let [term (first args)
              token (spotify/fetch-client-auth-token clientid secret)]
          (spotify/search-track token term))

        ;; output a list of pairs; the first item is the lastfm track and the second is the spotify track (or null)
        "lastfm-and-spotify"
        (let [lastfm-tracks (json/read (InputStreamReader. System/in) :key-fn keyword)
              token (spotify/fetch-client-auth-token clientid secret)]
          (map #(vector % (lastfm-to-spotify token %)) lastfm-tracks))

        "spotify-get-track"
        (let [id (first args)
              token (spotify/fetch-client-auth-token clientid secret)]
          (spotify/get-track token id))

        "spotify-auth-token" (spotify/fetch-client-auth-token clientid secret)

        "spotify-user-auth"
        (spotify/user-authorize clientid secret authserver (string/join " " args))

        "spotify-current-user"
        (spotify/get-current-user
         (spotify/fetch-user-auth-token clientid secret authserver))
        
        "spotify-current-user-playlists"
        (spotify/get-current-user-playlists
         (spotify/fetch-user-auth-token clientid secret authserver))

        "spotify-get-playlist"
        (let [playlist-id (first args)]
          (spotify/get-playlist
           (spotify/fetch-user-auth-token clientid secret authserver)
           playlist-id))

        "spotify-create-playlist"
        (let [token (spotify/fetch-user-auth-token clientid secret authserver "playlist-modify-public")
              user-id (:id (spotify/get-current-user token))
              playlist-name (first args)]
          (spotify/create-playlist token user-id playlist-name))

        "add-spotify-ids-to-playlist"
        (let [token (spotify/fetch-user-auth-token clientid secret authserver "playlist-modify-public")
              playlist-id (first args)
              track-ids (json/read (InputStreamReader. System/in))]
          (doseq [tids (page 100 track-ids)]
            (spotify/add-tracks-to-playlist token playlist-id (map #(str "spotify:track:" %) tids))))

        "tag-to-playlist"
        (let [token (spotify/fetch-user-auth-token clientid secret authserver "playlist-modify-public")
              [lastfm-user lastfm-tag & _] args
              user-id (:id (spotify/get-current-user token))
              playlist-name (str "last.fm tag: " lastfm-tag)
              uris (lastfm-user-tag-to-spotify-uris apikey lastfm-user lastfm-tag token)
              playlist-id (:id (spotify/create-playlist token user-id playlist-name))]

          (spotify/add-tracks-to-playlist token playlist-id uris)
          (spotify/get-playlist token playlist-id))

        ;; This doesn't work, API rate limit kicks in.
        "loved-to-playlist"
        (let [token (spotify/fetch-user-auth-token clientid secret authserver "playlist-modify-public")
              lastfm-user (first args)
              user-id (:id (spotify/get-current-user token))
              playlist-name (str "last.fm loved tracks for " lastfm-user)
              paged-uris (page 100 (lastfm-user-loved-to-spotify-uris apikey lastfm-user token))
              playlist-id (:id (spotify/create-playlist token user-id playlist-name))]

          (doseq [uris paged-uris]
            (spotify/add-tracks-to-playlist token playlist-id uris))
          (spotify/get-playlist token playlist-id)))))))
