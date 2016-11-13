(ns lucid.aether-test
  (:use hara.test)
  (:require [lucid.aether :refer :all]))

^{:refer lucid.aether/resolve-hierarchy :added "1.1"}
(fact " shows the dependency hierachy for all packages"
  
  (resolve-hierarchy '[midje "1.6.3"])
  => '{[midje/midje "1.6.3"]
       [{[ordered/ordered "1.2.0"] []}
        {[org.clojure/math.combinatorics "0.0.7"] []}
        {[org.clojure/core.unify "0.5.2"] []}
        {[utilize/utilize "0.2.3"]
         [{[org.clojure/tools.macro "0.1.1"] []}
          {[joda-time/joda-time "2.0"] []}
          {[ordered/ordered "1.0.0"] []}]}
        {[colorize/colorize "0.1.1"] []}
        {[org.clojure/tools.macro "0.1.5"] []}
        {[dynapath/dynapath "0.2.0"] []}
        {[swiss-arrows/swiss-arrows "1.0.0"] []}
        {[org.clojure/tools.namespace "0.2.4"] []}
        {[slingshot/slingshot "0.10.3"] []}
        {[commons-codec/commons-codec "1.9"] []}
        {[gui-diff/gui-diff "0.5.0"]
         [{[org.clojars.trptcolin/sjacket "0.1.3"]
           [{[net.cgrand/regex "1.1.0"] []}
            {[net.cgrand/parsley "0.9.1"]
             [{[net.cgrand/regex "1.1.0"] []}]}]}
          {[ordered/ordered "1.2.0"] []}]}
        {[clj-time/clj-time "0.6.0"]
         [{[joda-time/joda-time "2.2"] []}]}]})

^{:refer lucid.aether/resolve-dependencies :added "1.1"}
(fact "resolves maven dependencies for a set of coordinates"

  (resolve-dependencies '[prismatic/schema "1.1.3"])
  => '[[prismatic/schema "1.1.3"]]
  
  (resolve-dependencies '[midje "1.6.3"])
  => '[[utilize/utilize "0.2.3"]
       [swiss-arrows/swiss-arrows "1.0.0"]
       [slingshot/slingshot "0.10.3"]
       [org.clojure/tools.namespace "0.2.4"]
       [org.clojure/tools.macro "0.1.5"]
       [org.clojure/math.combinatorics "0.0.7"]
       [org.clojure/core.unify "0.5.2"]
       [org.clojars.trptcolin/sjacket "0.1.3"]
       [ordered/ordered "1.2.0"]
       [net.cgrand/regex "1.1.0"]
       [net.cgrand/parsley "0.9.1"]
       [midje/midje "1.6.3"]
       [joda-time/joda-time "2.2"]
       [gui-diff/gui-diff "0.5.0"]
       [dynapath/dynapath "0.2.0"]
       [commons-codec/commons-codec "1.9"]
       [colorize/colorize "0.1.1"]
       [clj-time/clj-time "0.6.0"]])

^{:refer lucid.aether/populate-artifacts :added "1.2"}
(fact "allows coordinate to fill rest of values"

  (populate-artifacts '[midje "1.6.3"]
                      {:artifacts [{:extension "pom"
                                    :file "midje.pom"}
                                   {:extension "jar"
                                    :file "midje.jar"}]})
  => {:artifacts [{:extension "pom",
                   :file "midje.pom",
                   :artifact "midje",
                   :group "midje",
                   :version "1.6.3"}
                  {:extension "jar",
                   :file "midje.jar",
                   :artifact "midje",
                   :group "midje",
                   :version "1.6.3"}]})

^{:refer lucid.aether/install :added "1.2"}
(comment "installs artifacts to the given coordinate"

  (install '[im.chit/hara.io.classpath "2.4.8"]
           {:artifacts [{:file "hara_io_classpath-2.4.8-jar"
                         :extension "jar"}
                        {:file "hara_io_classpath-2.4.8-pom"
                         :extension "pom"}]}))

^{:refer lucid.aether/deploy :added "1.2"}
(comment "deploys artifacts to the given coordinate"

  (deploy '[im.chit/hara.io.classpath "2.4.8"]
          {:artifacts [{:file "hara_io_classpath-2.4.8-jar"
                        :extension "jar"}
                       {:file "hara_io_classpath-2.4.8-pom"
                        :extension "pom"}
                        {:file "hara_io_classpath-2.4.8-pom.asc"
                         :extension "pom.asc"}
                       {:file "hara_io_classpath-2.4.8-jar.asc"
                        :extension "jar.asc"}]
           :repository {:id "clojars"
                        :url "https://clojars.org/repo/"
                        :authentication {:username "zcaudate"
                                         :password "hello"}}}))
