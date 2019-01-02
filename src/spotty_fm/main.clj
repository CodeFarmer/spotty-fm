(ns spotty-fm.main

  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [spotty-fm.lastfm :as lastfm]
            [spotty-fm.spotify :as spotify]
            [spotty-fm.core :refer [config]]
            [org.httpkit.server :as server])
  
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

  (println
   (json/write-str

    (case arg

      "lastfm-user-tag" (let [[user tag] args]
                          (lastfm/user-tagged-tracks (:apikey (:lastfm config)) user tag))
              
      "lastfm-user-loved" (let [user (first args)]
                            (lastfm/user-loved-tracks (:apikey (:lastfm config)) user))

      "spotify-search-tracks" (let [term (first args)
                                    token (:access_token (spotify/fetch-client-auth-token
                                                          (:clientid (:spotify config))
                                                          (:secret (:spotify config))))]
                                
                                (spotify/search-tracks token term))

      "spotify-search-track" (let [term (first args)
                                   token (:access_token (spotify/fetch-client-auth-token
                                                         (:clientid (:spotify config))
                                                         (:secret (:spotify config))))]
                               
                               (spotify/search-track token term))))))
