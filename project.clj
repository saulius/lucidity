(defproject im.chit/vinyasa "0.4.1"
  :description "Utilities to make the development process smoother"
  :url "http://www.github.com/zcaudate/vinyasa"
  :license {:name "The MIT License"
            :url "http://http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [im.chit/hara.reflect "2.2.11"]
                 [version-clj/version-clj "0.1.2"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]
                                  [leiningen #=(leiningen.core.main/leiningen-version)]
                                  [com.jcraft/jsch "0.1.51"]]
                   :plugins [;;[lein-repack "0.2.10"]
                             [lein-midje "3.1.3"]]}})
