(ns spotty-fm.spotify

  (:require [clojure.data.json :as json]
            [clojure.java.browse :refer [browse-url]]
            [clojure.string :as string]
            [clojure.walk :refer [prewalk]]
            
            [environ.core :refer [env]]
            [org.httpkit.client :as http])
  
  (:import java.util.Base64
           java.net.URLEncoder))


;; Stolen from the source code of http-kit, which could just have made
;; it public but Java programmers gonna Java, even in Clojure.

(defn url-encode [s] (URLEncoder/encode (str s) "utf8"))

(defn encode-base64 [to-encode]
  (.encodeToString (Base64/getEncoder) (.getBytes to-encode)))

(defn nested-param [params]
  (prewalk (fn [d]
             (if (and (vector? d) (map? (second d)))
               (let [[fk m] d]
                 (reduce (fn [m [sk v]]
                           (assoc m (str (name fk) \[ (name sk) \]) v))
                         {} m))
               d))
           params))

(defn query-string
  "Returns URL-encoded query string for given params map."
  [m]
  (let [m (nested-param m)
        param (fn [k v]  (str (url-encode (name k)) "=" (url-encode v)))
        join  (fn [strs] (string/join "&" strs))]
    (join (for [[k v] m] (if (sequential? v)
                           (join (map (partial param k) (or (seq v) [""])))
                           (param k v))))))

;; ^ end theft from http-kit

(defn basic-auth-header [clientid secret]
  {"Authorization" (str "Basic "  (encode-base64 (str clientid \: secret)))})


(defn bearer-auth-header [token]
  { "Authorization" (str "Bearer " token)})

;; API client authorization

(defn -fetch-client-auth-token [clientid secret]
  (let [{:keys [status headers body error] :as resp}
        @(http/post "https://accounts.spotify.com/api/token" {:headers (basic-auth-header clientid secret)
                                                              :form-params { :grant_type "client_credentials"}})]
    (json/read-str body :key-fn keyword)))

(defn fetch-client-auth-token [clientid secret]
  (if-let [override (env :spotify-auth-token)]
    override
    (:access_token (-fetch-client-auth-token clientid secret))))

;; User authorization

(defn rand-str [len]
  (string/join (repeatedly len #(char (+ (rand 26) 65)))))

(defn -user-authorize!
  "Initiate the user authorization flow by opening a browser window and passing a state string to identify the auth request"
  [client-id auth-server state]
  (browse-url (str "https://accounts.spotify.com/authorize?" (query-string {:client_id client-id
                                                                            :response_type "code"
                                                                            :redirect_uri (str auth-server "/authorized")
                                                                            :state state}))))

(defn -retrieve-auth-code
  "Given a state string, make a blocking call to a spotty-auth server to get the associated authorization code"
  [auth-server state]
  (let [{:keys [status headers body error] :as resp}
        @(http/get (str auth-server "/token/" state))]
    body))

(defn -user-auth-for-code
  "Exchange a user auth code for an authorization object containing auth token, a TTL and a refresh token. The auth server is also passed as a verification parameter"
  [client-id secret auth-server code]
    (let [{:keys [status headers body error] :as resp}
          @(http/post "https://accounts.spotify.com/api/token"
                      {:headers (basic-auth-header client-id secret)
                       :form-params {:grant_type "authorization_code"
                                     :code code
                                     :redirect_uri (str auth-server "/authorized")}})]
      
      (json/read-str body :key-fn keyword)))

;; TODO add scope arg
(defn user-authorize
  "Initiate the user auth flow in a browser, then coordinate with spotty-auth server and Spotify to retrieve the user authorization object (TTL, auth and refresh tokens)"
  [client-id secret auth-server]
  (let [state (rand-str 64)]
    (-user-authorize! client-id auth-server state)
    (-user-auth-for-code client-id secret auth-server (-retrieve-auth-code auth-server state))))

(defn fetch-user-auth-token
  "Convenience function: check for SPOTIFY_AUTH_TOKEN environment variable; if none exists then start the user auth flow in a browser, extract the token immediately discarding the other auth info (such as refresh)."
  [client-id secret auth-server]
  (if-let [override (env :spotify-auth-token)]
    override
    (:access_token (user-authorize client-id secret auth-server))))

;; Tracks

(defn simple-track [spotify-track]
  {:title (:name spotify-track)
   :artist (:name  (first  (:artists spotify-track))) ; hmm
   :isrc (:isrc (:external_ids spotify-track))
   :spotify-id (:id spotify-track)})

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

;; User

(defn get-current-user
  "Retrieve basic user info for the user associated with valid user authorization token"
  [token]
  (let [{:keys [status headers body error] :as resp}
        @(http/get "https://api.spotify.com/v1/me"
                   {:headers (bearer-auth-header token)})]
    (json/read-str body :key-fn keyword)))
