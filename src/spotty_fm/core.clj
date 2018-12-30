(ns spotty-fm.core
  (:require [clojure.data.json :as json]
            [environ.core :refer [env]])
  (:gen-class))



(comment {:lastfm  (json/read-str (slurp (str (env :home) \/ "Dropbox/last.fm.json"))
                                    :key-fn keyword)
          :spotify (json/read-str (slurp (str (env :home) \/ "Dropbox/spotify.json"))
                                  :key-fn keyword)})

(def config

  (json/read-str (slurp (str (env :pwd) \/ "config.json"))))
