(ns spotty-fm.spotify-test
  (:require [clojure.test :refer :all]
            [spotty-fm.spotify :refer :all]
            [spotty-fm.core :refer [config]]))


(deftest test-client-auth

  (testing "Connecting to the live Spotify service, the clientID and secret should be accepted and a token returned"
    (let [resp (fetch-client-auth-token (:clientid (:spotify config))
                                        (:secret (:spotify config)))]

      (is (contains? resp :access_token) "Client auth response should contain a token field")
      (is (= "Bearer" (:token_type resp)) "The client auth response should be a bearer token"))))


(deftest test-search-tracks

  (testing "Connecting to the live Spotify service, track search should work"
    (let [t (:access_token (fetch-client-auth-token (:clientid (:spotify config))
                                                    (:secret (:spotify config))))
          resp (-search-tracks t "DJ Shadow Building Steam With A Grain Of Salt")]

      (is (contains? resp :tracks) "The search response should contain a tracks field")
      (is (> (count (:items (:tracks resp))) 0) "There should be one or more track items in the search response"))))
