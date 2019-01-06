(ns spotty-fm.spotify-test
  (:require [clojure.test :refer :all]
            [spotty-fm.spotify :refer :all]
            [spotty-fm.core :refer [config]]))


(deftest test-client-auth-call

  (testing "Connecting to the live Spotify service, the clientID and secret should be accepted and a token returned"
    (let [resp (fetch-client-auth-token (:clientid (:spotify config))
                                        (:secret (:spotify config)))]

      (is (contains? resp :access_token) "Client auth response should contain a token field")
      (is (= "Bearer" (:token_type resp)) "The client auth response should be a bearer token"))))


(deftest test-search-tracks-call

  (testing "Connecting to the live Spotify service, track search should work"
    (let [t (:access_token (fetch-client-auth-token (:clientid (:spotify config))
                                                    (:secret (:spotify config))))
          resp (-search-tracks t "DJ Shadow Building Steam With A Grain Of Salt")]

      (is (contains? resp :tracks) "The search response should contain a tracks field")
      (is (> (count (:items (:tracks resp))) 0) "There should be one or more track items in the search response"))))


(deftest test-search-tracks

  (let [t (:access_token (fetch-client-auth-token (:clientid (:spotify config))
                                                  (:secret (:spotify config))))]
    
    (testing "Wrapped track search call success"

      (is (not (empty? (search-tracks t "Breeders Last Splash"))) "Successful call should return non-empty seq"))

    (testing "Wrapped track search call failure"

      (let [result (search-tracks t "Wharrgarrbl Snickety Sneebo")]

        (is (seq? result) "Result should be a sequence")
        (is (empty? result) "Search should return an empty list")))

    (testing "Wrapped track search call error"

      (let [result (search-tracks "NOT_A_TOKEN" "Breeders Last Splash")]

        (is (seq? result) "Result should be a sequence")
        (is (empty? result) "Search should return an empty list")))))


(deftest test-get-track-call

  (testing "Connecting to the live Spotify service, track retrieval should work"
    (let [t (:access_token (fetch-client-auth-token (:clientid (:spotify config))
                                                    (:secret (:spotify config))))
          spotify-id "1V7mHn6zEEUpgysBYxiW9r"
          resp (-get-track t spotify-id)]

      (is (= "2 Atoms In A Molecule" (:name resp)) "The search response should contain a name field")
      (is (= spotify-id (:id resp)) "The returned track should have the same ID as passed in the requed"))))

(deftest test-simple-track

  (let [spotify-track {:disc_number 1,
                       :popularity 22,
                       :duration_ms 252866,
                       :name "Skip Tracer",
                       :explicit false,
                       :type "track",
                       :external_urls {:spotify "https://open.spotify.com/track/7p5iMpBmTU04vIrjAk0mJc"},
                       :external_ids {:isrc "USGF19582510"},
                       :preview_url nil,
                       :track_number 10,
                       :is_local false,
                       :id "7p5iMpBmTU04vIrjAk0mJc",
                       :available_markets ["AD" "AR" "AT" "AU" "BE" "BG" "BO" "BR" "CA" "CH" "CL" "CO" "CR" "CY" "CZ" "DE" "DK" "DO" "EC" "EE" "ES" "FI" "FR" "GB" "GR" "GT" "HK" "HN" "HU" "ID" "IE" "IL" "IS" "IT" "JP" "LI" "LT" "LU" "LV" "MC" "MT" "MX" "MY" "NI" "NL" "NO" "NZ" "PA" "PE" "PH" "PL" "PT" "PY" "RO" "SE" "SG" "SK" "SV" "TH" "TR" "TW" "US" "UY" "VN" "ZA"],
                       :uri "spotify:track:7p5iMpBmTU04vIrjAk0mJc",
                       :artists [{:external_urls {:spotify "https://open.spotify.com/artist/5UqTO8smerMvxHYA5xsXb6"}, :href "https://api.spotify.com/v1/artists/5UqTO8smerMvxHYA5xsXb6", :id "5UqTO8smerMvxHYA5xsXb6", :name "Sonic Youth", :type "artist", :uri "spotify:artist:5UqTO8smerMvxHYA5xsXb6"}],
                       :album {:album_type "album",
                               :release_date "1995-01-01",
                               :images [{:height 640, :url "https://i.scdn.co/image/83611f8684d6f39560418eff2f53cc9547af20fd", :width 640}
                                        {:height 300, :url "https://i.scdn.co/image/2c1e5f0231d955d62439ba562eb211e7614a2745", :width 300}
                                        {:height 64, :url "https://i.scdn.co/image/9a1bb01b1fae00bff7fb8bdc0086b24237d62e68", :width 64}],
                               :name "Washing Machine",
                               :release_date_precision "day",
                               :type "album",
                               :external_urls {:spotify "https://open.spotify.com/album/0VskfMaczM0MNAlqqvokTC"},
                               :id "0VskfMaczM0MNAlqqvokTC",
                               :available_markets ["AD" "AR" "AT" "AU" "BE" "BG" "BO" "BR" "CA" "CH" "CL" "CO" "CR" "CY" "CZ" "DE" "DK" "DO" "EC" "EE" "ES" "FI" "FR" "GB" "GR" "GT" "HK" "HN" "HU" "ID" "IE" "IL" "IS" "IT" "JP" "LI" "LT" "LU" "LV" "MC" "MT" "MX" "MY" "NI" "NL" "NO" "NZ" "PA" "PE" "PH" "PL" "PT" "PY" "RO" "SE" "SG" "SK" "SV" "TH" "TR" "TW" "US" "UY" "VN" "ZA"],
                               :uri "spotify:album:0VskfMaczM0MNAlqqvokTC",
                               :artists [{:external_urls {:spotify "https://open.spotify.com/artist/5UqTO8smerMvxHYA5xsXb6"}, :href "https://api.spotify.com/v1/artists/5UqTO8smerMvxHYA5xsXb6", :id "5UqTO8smerMvxHYA5xsXb6", :name "Sonic Youth", :type "artist", :uri "spotify:artist:5UqTO8smerMvxHYA5xsXb6"}],
                               :total_tracks 11,
                               :href "https://api.spotify.com/v1/albums/0VskfMaczM0MNAlqqvokTC"},
                       :href "https://api.spotify.com/v1/tracks/7p5iMpBmTU04vIrjAk0mJc"}

        st (simple-track spotify-track)]

    (is (= "7p5iMpBmTU04vIrjAk0mJc" (:spotify-id st)) "simple-track should extract the correct spotify-id from the track response object")))
