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
;; although :limit 1000 avoids it a bit
(defn -user-tagged-tracks [api-key user tag]
  (let [{:keys [status headers body error] :as resp}
        @(http/get "http://ws.audioscrobbler.com/2.0/" {:query-params {:method "user.getpersonaltags"
                                                                       :user user
                                                                       :api_key api-key
                                                                       :format "json"
                                                                       :taggingtype "track"
                                                                       :tag tag
                                                                       :limit 1000}})]
    (if error
      (do
        (println error)
        nil)
      (json/read-str body :key-fn keyword))))

(defn user-tagged-tracks [api-key user tag]
  (if-let [resp (-user-tagged-tracks api-key user tag)]
    (map simple-track (get-in resp [:taggings :tracks :track]))))


;; FIXME this does not cope with pagination
;; although :limit 1000 puts that off a bit
(defn -user-loved-tracks [api-key user]
  (let [{:keys [status headers body error] :as resp}
        @(http/get "http://ws.audioscrobbler.com/2.0/" {:query-params {:method "user.getlovedtracks"
                                                                       :user user
                                                                       :api_key api-key
                                                                       :format "json"
                                                                       :limit 1000}})]
    (if error
      (do
        (println error)
        nil)
      (json/read-str body :key-fn keyword))))

(defn user-loved-tracks [api-key user]
  (if-let [resp (-user-loved-tracks api-key user)]
    (map simple-track (get-in resp [:lovedtracks :track]))))

(defn -user-tags [api-key user]
  (let [{:keys [status headers body error] :as resp}
        @(http/get "http://ws.audioscrobbler.com/2.0/" {:query-params {:method "user.gettoptags"
                                                                       :user user
                                                                       :api_key api-key
                                                                       :format "json"
                                                                       :limit 1000}})]
    (if error
      (do
        (println error)
        nil)
      (json/read-str body :key-fn keyword))))

(defn user-tags [api-key user]
  (if-let [resp (-user-tags api-key user)]
    (get-in resp [:toptags :tag])))
;; pagination hint: you can get the metadata from a loved-tracks response (for example) like this:
;; (get-in (fetch-user-loved-tracks (:apikey (:lastfm config)) "CodeFarmer") [:lovedtracks (keyword "@attr")])


