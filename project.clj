(defproject spotty-fm "0.1.0-SNAPSHOT"

  :description "Using last.fm and spotify data together"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [environ "1.1.0"]
                 [http-kit "2.4.0-alpha2"]] ;; http-kit 2.3.0 has problems with jdk11

  :main ^:skip-aot spotty-fm.main
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
