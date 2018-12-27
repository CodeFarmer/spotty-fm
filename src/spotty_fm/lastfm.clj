(ns spotty-fm.lastfm

  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]])
  (:gen-class))


(defn simple-track [lastfm-track]
  {:title (:name lastfm-track)
   :artist (:name (:artist lastfm-track))
   :mbid (:mbid lastfm-track)})


;; FIXME this does not cope with pagination
(defn fetch-user-tagged-tracks [api-key user tag]
  (let [{:keys [status headers body error] :as resp}
        @(http/get "http://ws.audioscrobbler.com/2.0/" {:query-params {:method "user.getpersonaltags"
                                                                       :user user
                                                                       :api_key api-key
                                                                       :format "json"
                                                                       :taggingtype "track"
                                                                       :tag tag}})]
    (if error
      (do
        (println error)
        nil)
      (json/read-str body :key-fn keyword))))


;; FIXME this does not cope with pagination
(defn fetch-user-loved-tracks [api-key user]
  (let [{:keys [status headers body error] :as resp}
        @(http/get "http://ws.audioscrobbler.com/2.0/" {:query-params {:method "user.getlovedtracks"
                                                                       :user user
                                                                       :api_key api-key
                                                                       :format "json"}})]
    (if error
      (do
        (println error)
        nil)
      (json/read-str body :key-fn keyword))))
