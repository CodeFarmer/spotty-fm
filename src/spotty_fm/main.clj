(ns spotty-fm.main

  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]]
            [spotty-fm.lastfm :as lastfm]
            [org.httpkit.server :as server])
  
  (:gen-class))

(def config
  {:lastfm  (json/read-str (slurp (str (env :home) \/ "Dropbox/last.fm.json"))
                           :key-fn keyword)
   :spotify (json/read-str (slurp (str (env :home) \/ "Dropbox/spotify.json"))
                           :key-fn keyword)})

(defn accept-spotify-auth [req]
   {:status  200
    :headers {"Content-Type" "text/html"}
    :body    "Thanks!"})

(def spotify-auth-url (str "https://accounts.spotify.com/authorize?response_type=code"
                           "&client_id=" (:clientid (:spotify config))
                           "&scope=playlist-modify-private"))

(defn -main [& args]
  (println (lastfm/fetch-user-tagged-tracks (:apikey (:lastfm) config) "CodeFarmer" "spine melting")))
