(ns spotty-fm.main-test
  (:require [clojure.test :refer :all]
            [spotty-fm.main :refer :all]
            [spotty-fm.core :refer [config]]
            [spotty-fm.spotify :as spotify]))


(deftest test-lastfm-to-spotify

  (let [t (spotify/fetch-client-auth-token (:clientid (:spotify config))
                                           (:secret (:spotify config)))]
    (testing "Known lastfm track to known spotify track"
      
      (let [spotify-track (lastfm-to-spotify t {:title "Any Day Now",
                                                :artist "Elbow",
                                                :mbid "f93eff49-4068-4074-99a9-1b61ea06f530"})]
        
        (is (= spotify-track {:title "Any Day Now"
                              :artist "Elbow"
                              :isrc "GBBLK0000224"
                              :spotify-id "59qdWok2u5ItLGEVf008kD"})
            "Known spotify track should be returned for known lastfm track")))))


(deftest test-page
  (let [alist [1 2 3 4 5 6 7 8 9]]
    (is (= [1 2 3] (first (page 3 alist))))
    (is (= [9] (last (page 2 alist))))))
