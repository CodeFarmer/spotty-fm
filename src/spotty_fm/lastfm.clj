(ns spotty-fm.lastfm

  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]])
  (:gen-class))

;; (first (:track (:tracks (:taggings (fetch-user-tagged-tracks key "CodeFarmer" "spine melting"))))

(defn simple-track [lastfm-track]
  {:title (:name lastfm-track)
   :artist (:name (:artist lastfm-track))
   :mbid (:mbid lastfm-track)})

(defn fetch-user-tagged-tracks [api-key user tag]
  (let [{:keys [status headers body error] :as resp}
        @(http/get "http://ws.audioscrobbler.com/2.0/" {:query-params {:method "user.getpersonaltags"
                                                                       :user user
                                                                       :api_key api-key
                                                                       :format "json"
                                                                       :taggingtype "track"
                                                                       :tag tag}})]
    (json/read-str body :key-fn keyword)))
