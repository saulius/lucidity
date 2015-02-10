(ns vinyasa.maven.jar-test
  (:require [vinyasa.maven.jar :refer :all]
            [vinyasa.maven.file :as file]
            [midje.sweet :refer :all]))

(fact "maven-file"
  (maven-file '[org.clojure/clojure "1.5.1"])
  => (str *local-repo* "/org/clojure/clojure/1.5.1/clojure-1.5.1.jar")

  (maven-file '[org.clojure/clojure "1.5.1"] ".pom")
  => (str *local-repo* "/org/clojure/clojure/1.5.1/clojure-1.5.1.pom")

  (maven-file '[org.clojure/clojure "1.5.1"] ".pom" "/usr/local/maven/repository")
  => "/usr/local/maven/repository/org/clojure/clojure/1.5.1/clojure-1.5.1.pom")

(fact "resolve-jar"
  (resolve-jar String)
  => [(str file/*java-home* "/lib/rt.jar") "java/lang/String.class"]

  (resolve-jar String
               :jar-paths
               [(str file/*java-home* "/lib/rt.jar")])
  => [(str file/*java-home* "/lib/rt.jar") "java/lang/String.class"]

  (resolve-jar 'clojure.core
               :jar-paths
               [(str file/*java-home* "/lib/rt.jar")])
  => nil

  (resolve-jar 'clojure.core
               :jar-paths
               [(str *local-repo* "/org/clojure/clojure/1.6.0/clojure-1.6.0.jar")])
  => [(str *local-repo* "/org/clojure/clojure/1.6.0/clojure-1.6.0.jar")
      "clojure/core.clj"]

  (resolve-jar 'clojure.core
               :coordinate
               '[org.clojure/clojure "1.6.0"])
  => [(str *local-repo* "/org/clojure/clojure/1.6.0/clojure-1.6.0.jar") "clojure/core.clj"])
