(defproject im.chit/vinyasa "0.2.2"
  :description "Utilities to make the development process smoother"
  :url "http://www.github.com/zcaudate/vinyasa"
  :license {:name "The MIT License"
            :url "http://http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.cemerick/pomegranate "0.3.0"]
                 [im.chit/iroh "0.1.11"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]
                                  [leiningen #=(leiningen.core.main/leiningen-version)]]
                   :plugins [[lein-repack "0.1.4"]
                             [lein-midje "3.1.3"]]}})
