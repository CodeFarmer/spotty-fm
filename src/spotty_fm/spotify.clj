(ns spotty-fm.spotify

  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]])
  (:import java.util.Base64))

(defn encode-base64 [to-encode]
  (.encodeToString (Base64/getEncoder) (.getBytes to-encode)))

(defn basic-auth-header [clientid secret]
  {"Authorization" (str "Basic "  (encode-base64 (str clientid \: secret)))})

(defn -fetch-client-auth-token [clientid secret]
  (let [{:keys [status headers body error] :as resp}
        @(http/post "https://accounts.spotify.com/api/token" {:headers (basic-auth-header clientid secret)
                                                              :form-params { :grant_type "client_credentials"}})]
    (json/read-str body :key-fn keyword)))

(defn fetch-client-auth-token [clientid secret]
  (if-let [override (env :spotify-auth-token)]
    override
    (:access_token (-fetch-client-auth-token clientid secret))))


(defn simple-track [spotify-track]
  {:title (:name spotify-track)
   :artist (:name  (first  (:artists spotify-track))) ; hmm
   :isrc (:isrc (:external_ids spotify-track))
   :spotify-id (:id spotify-track)})


(defn bearer-auth-header [token]
  { "Authorization" (str "Bearer " token)})

;; (first (:items (:tracks (search-tracks t "Sonic Youth Becuz)))
(defn -search-tracks [token q]
  (let [{:keys [status headers body error] :as resp}
        @(http/get "https://api.spotify.com/v1/search" {:headers (bearer-auth-header token)
                                                        :query-params {:type "track"
                                                                       :q q}})]
    (json/read-str body :key-fn keyword)))

(defn search-tracks [token q]
  (if-let [resp (-search-tracks token q)]
    (map simple-track (get-in resp [:tracks :items]))))

;; NOTE you can do (search-track t "isrc:FR6P11500950")
(defn search-track [token q]
  (first (search-tracks token q)))


(defn -get-track [token id]
  (let [{:keys [status headers body error] :as resp}
        @(http/get (str "https://api.spotify.com/v1/tracks/" id)
                   {:headers (bearer-auth-header token)})]
    (json/read-str body :key-fn keyword)))

(defn get-track [token id]
  (simple-track (-get-track token id)))
