(ns lucid.aether-test
  (:use hara.test)
  (:require [lucid.aether :refer :all]))

^{:refer lucid.aether/aether :added "1.1"}
(fact "creates an `Aether` object"

  (into {} (aether))
  => +defaults+)

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
