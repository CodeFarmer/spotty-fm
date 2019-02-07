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

(defn lastfm-user-tag-to-spotify-uris
  [lastfm-api-key lastfm-user tag spotify-auth-token]
  (->> (lastfm/user-tagged-tracks lastfm-api-key lastfm-user tag)
       (map #(lastfm-to-spotify spotify-auth-token %))
       (filter (complement nil?))
       (map #(str "spotify:track:" (:spotify-id %)))))

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

        "tag-to-playlist"
        (let [token (spotify/fetch-user-auth-token clientid secret authserver "playlist-modify-public")
              [lastfm-user lastfm-tag & _] args
              user-id (:id (spotify/get-current-user token))
              playlist-name (str "last.fm tag: " lastfm-tag)
              uris (lastfm-user-tag-to-spotify-uris apikey lastfm-user lastfm-tag token)
              playlist-id (:id (spotify/create-playlist token user-id playlist-name))]

          (spotify/add-tracks-to-playlist token playlist-id uris)
          (spotify/get-playlist token playlist-id)))))))
