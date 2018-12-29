(ns spotty-fm.main

  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [spotty-fm.lastfm :as lastfm]
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

(defn -main [arg & args]

  (println
   (json/write-str

    (case arg

      "user-tag" (let [[user tag] args]
                   (if-let [resp (lastfm/fetch-user-tagged-tracks (:apikey (:lastfm config)) user tag)]
                     (map lastfm/simple-track (get-in resp [:taggings :tracks :track]))))
              
      "user-loved" (let [user (first args)]
                     (if-let [resp (lastfm/fetch-user-loved-tracks (:apikey (:lastfm config)) user)]
                       (map lastfm/simple-track (get-in resp [:lovedtracks :track]))))))))
