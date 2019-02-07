(ns spotty-fm.main

  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [spotty-fm.lastfm :as lastfm]
            [spotty-fm.spotify :as spotify]
            [spotty-fm.core :refer [config]]
            [org.httpkit.server :as server])

  (:import java.io.InputStreamReader)
  
  (:gen-class))


(defn accept-spotify-auth [req]
   {:status  200
    :headers {"Content-Type" "text/html"}
    :body    "Thanks!"})

(def spotify-auth-url (str "https://accounts.spotify.com/authorize?response_type=code"
                           "&client_id=" (:clientid (:spotify config))
                           "&scope=playlist-modify-private"))


(defn lastfm-to-spotify [token simple-track]
  (spotify/search-track
   token (str (:artist simple-track) " " (:title simple-track))))


(defn -main [arg & args]


  (let [{:keys [apikey]} (:lastfm config)
        {:keys [clientid secret authserver]} (:spotify config)]
    
    (println
     
     (json/write-str

      (case arg

        "lastfm-user-tag" (let [[user tag] args]
                            (lastfm/user-tagged-tracks apikey user tag))
        
        "lastfm-user-loved" (let [user (first args)]
                              (lastfm/user-loved-tracks apikey user))

        "spotify-search-tracks" (let [term (first args)
                                      token (spotify/fetch-client-auth-token clientid secret)]
                                  
                                  (spotify/search-tracks token term))

        "spotify-search-track" (let [term (first args)
                                     token (spotify/fetch-client-auth-token clientid secret)]
                                 
                                 (spotify/search-track token term))

        ;; output a list of pairs; the first item is the lastfm track and the second is the spotify track (or null)
        "lastfm-and-spotify" (let [lastfm-tracks (json/read (InputStreamReader. System/in) :key-fn keyword)
                                   token (spotify/fetch-client-auth-token clientid secret)]
                               (map #(vector % (lastfm-to-spotify token %)) lastfm-tracks))

        "spotify-get-track" (let [id (first args)
                                  token (spotify/fetch-client-auth-token clientid secret)]
                              
                              (spotify/get-track token id))

        "spotify-auth-token" (spotify/fetch-client-auth-token clientid secret)

        "spotify-user-auth" (spotify/user-authorize clientid secret authserver)

        "spotify-current-user" (spotify/get-current-user
                                (spotify/fetch-user-auth-token clientid secret authserver))
        
        "spotify-current-user-playlists" (spotify/get-current-user-playlists
                                          (spotify/fetch-user-auth-token clientid secret authserver))

        "spotify-get-playlist" (let [playlist-id (first args)]
                                 (spotify/get-playlist
                                  (spotify/fetch-user-auth-token clientid secret authserver)
                                  playlist-id)))))))
