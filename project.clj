(defproject im.chit/vinyasa "0.4.7"
  :description "Utilities to make the development process smoother"
  :url "http://www.github.com/zcaudate/vinyasa"
  :license {:name "The MIT License"
            :url "http://http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [im.chit/hara.reflect "2.3.7"]
                 [im.chit/wu.kong "0.1.2"]
                 [version-clj/version-clj "0.1.2"]]
  :profiles {:dev {:dependencies [[im.chit/lucid "0.9.7"]
                                  [leiningen #=(leiningen.core.main/leiningen-version)]
                                  [com.jcraft/jsch "0.1.51"]]
                   :plugins [[lein-repack "0.2.10"]
                             [com.jakemccrary/lein-test-refresh "0.14.0"]]}})
