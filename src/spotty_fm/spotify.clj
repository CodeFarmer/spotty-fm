(ns spotty-fm.spotify

  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]])
  (:import java.util.Base64))

(defn encode-base64 [to-encode]
  (.encodeToString (Base64/getEncoder) (.getBytes to-encode)))

(defn basic-auth-header [clientid secret]
  (str "Basic "  (encode-base64 (str clientid \: secret))))


(defn fetch-client-auth-token [clientid secret]
  (let [{:keys [status headers body error] :as resp}
        @(http/post "https://accounts.spotify.com/api/token" {:headers { "Authorization" (basic-auth-header clientid secret)}
                                                              :form-params { :grant_type "client_credentials"}})]
    (json/read-str body :key-fn keyword)))
  

(defn simple-track [lastfm-track]
  {:title (:name lastfm-track)
   :artist (:name  (first  (:artists lastfm-track))) ; hmm
   :isrc (:isrc (:external_ids lastfm-track))})

;; (first (:items (:tracks (search-tracks t "Sonic Youth Becuz)))
(defn search-tracks [token q]
  (let [{:keys [status headers body error] :as resp}
        @(http/get "https://api.spotify.com/v1/search" {:headers { "Authorization" (str "Bearer " token)}
                                                        :query-params {:type "track"
                                                                       :q q}})]
    (json/read-str body :key-fn keyword)))
