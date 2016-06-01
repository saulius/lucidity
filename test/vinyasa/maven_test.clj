(ns vinyasa.maven-test
  (:use lucid.sweet)
  (:require [vinyasa.maven :refer :all]
            [vinyasa.maven.jar :as jar]
            [wu.kong :as aether]))

(fact "coordinate-dependecies"
  (coordinate-dependencies '[[midje "1.6.3"]])
  => (just '[[midje/midje "1.6.3"]
             [ordered/ordered "1.2.0"]
             [org.clojure/math.combinatorics "0.0.7"]
             [org.clojure/core.unify "0.5.2"]
             [utilize/utilize "0.2.3"]
             [org.clojure/tools.macro "0.1.1"]
             [joda-time/joda-time "2.0"]
             [ordered/ordered "1.0.0"]
             [colorize/colorize "0.1.1"]
             [org.clojure/tools.macro "0.1.5"]
             [dynapath/dynapath "0.2.0"]
             [swiss-arrows/swiss-arrows "1.0.0"]
             [org.clojure/tools.namespace "0.2.4"]
             [slingshot/slingshot "0.10.3"]
             [commons-codec/commons-codec "1.9"]
             [gui-diff/gui-diff "0.5.0"]
             [org.clojars.trptcolin/sjacket "0.1.3"]
             [net.cgrand/regex "1.1.0"]
             [net.cgrand/parsley "0.9.1"]
             [net.cgrand/regex "1.1.0"]
             [ordered/ordered "1.2.0"]
             [clj-time/clj-time "0.6.0"]
             [joda-time/joda-time "2.2"]]
           :in-any-order)) 

(fact "resolve-jar"
  (-> (resolve-jar "clojure/core.clj") second)
  => "clojure/core.clj"

  (-> (resolve-jar 'clojure.core) second)
  => "clojure/core.clj"

  (-> (resolve-jar 'version-clj.core) second)
  => "version_clj/core.clj"

  (-> (resolve-jar com.jcraft.jsch.Channel) second)
  => "com/jcraft/jsch/Channel.class"

  (-> (resolve-jar 'com.jcraft.jsch.Channel) second)
  => "com/jcraft/jsch/Channel.class")

(fact "resolve-coordinates"
  (resolve-coordinates "clojure/core.clj")
  => '[org.clojure/clojure "1.8.0"]

  (resolve-coordinates 'com.jcraft.jsch.Channel)
  => '[com.jcraft/jsch "0.1.51"]

  (resolve-coordinates 'version-clj.core)
  => '[version-clj/version-clj "0.1.2"])

(fact "resolve-with-deps"
  (resolve-with-deps 'clojure.core)
  => [(str jar/*local-repo* "/org/clojure/clojure/1.8.0/clojure-1.8.0.jar")
      "clojure/core.clj"]

  (resolve-with-deps 'version-clj.core)
  => [(str jar/*local-repo* "/version-clj/version-clj/0.1.2/version-clj-0.1.2.jar")
      "version_clj/core.clj"]

  (resolve-with-deps 'dynapath.util ['midje "1.6.1"])
  => [(str jar/*local-repo* "/dynapath/dynapath/0.2.0/dynapath-0.2.0.jar")
      "dynapath/util.clj"]

  (resolve-with-deps 'dynapath.util (str jar/*local-repo* "/midje/midje/1.6.1/midje-1.6.1.jar"))
  => [(str jar/*local-repo* "/dynapath/dynapath/0.2.0/dynapath-0.2.0.jar")
      "dynapath/util.clj"]

  (resolve-with-deps 'midje.sweet ['midje "1.6.1"])
  => [(str jar/*local-repo* "/midje/midje/1.6.1/midje-1.6.1.jar")
      "midje/sweet.clj"])
