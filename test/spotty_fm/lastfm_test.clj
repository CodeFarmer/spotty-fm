(ns spotty-fm.lastfm-test
  (:require [clojure.test :refer :all]
            [spotty-fm.lastfm :refer :all]
            [spotty-fm.core :refer [config]]))


(deftest test-user-tagged

  (testing "Connecting to the live last.fm server, retrieve a user's tagged tracks"
    (let [resp (fetch-user-tagged-tracks (:apikey (:lastfm config)) "CodeFarmer" "ohrwurm")]

      (is (contains? resp :taggings) "Response should contain a taggings element")
      (is (contains? (:taggings resp) :tracks) "taggings should contain a tracks element")
      (is (contains? (:tracks (:taggings resp)) :track) "tracks should contain a track element")
      (is (> (count (get-in resp [:taggings :tracks :track])) 0) "Response should contain one or more tagged tracks"))))


(deftest test-user-loved

  (testing "Connecting to the live last.fm server, retrieve a user's tagged tracks"
    (let [resp (fetch-user-loved-tracks (:apikey (:lastfm config)) "CodeFarmer")]

      (is (contains? resp :lovedtracks) "Response should contain a lovedtracks element")
      (is (contains? (:lovedtracks resp) :track) "lovedtracks should contain a track element")
      (is (> (count (get-in resp [:lovedtracks :track])) 0) "Response should contain one or more loved tracks"))))

