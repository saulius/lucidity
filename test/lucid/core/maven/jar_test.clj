(ns lucid.core.maven.jar-test
  (:use hara.test)
  (:require [lucid.core.maven.jar :refer :all]
            [lucid.core.maven.file :as file]
            [hara.io.file :as fs]
            [clojure.java.io :as io]))

(def ^:dynamic *match-version* "0.2.2")

(def ^:dynamic *match-path*
  "/Users/chris/.m2/repository/org/clojure/core.match/0.2.2/core.match-0.2.2.jar"
  (str (fs/path "~/.m2/repository/org/clojure/core.match/"
                *match-version*
                (str "/core.match-"
                     *match-version*
                     ".jar"))))

^{:refer lucid.core.maven.jar/jar-file :added "1.1"}
(fact "returns a path as a jar or nil if it does not exist"

  (jar-file *match-path*)
  => java.util.jar.JarFile)

^{:refer lucid.core.maven.jar/jar-entry :added "1.1"}
(fact "returns an entry of the jar or nil if it does not exist"

  (jar-entry *match-path* "clojure/core/match.clj")
  => java.util.jar.JarFile$JarFileEntry

  (jar-entry *match-path* "NON-FILE")
  => nil)

^{:refer lucid.core.maven.jar/jar-stream :added "1.1"}
(fact "gets the input-stream of the entry for the jar"
  
  (-> (java.io.File. *match-path*)
      (jar-stream "clojure/core/match.clj")
      (java.io.InputStreamReader.)
      (java.io.PushbackReader.)
      (read)
      second)
  => 'clojure.core.match)

^{:refer lucid.core.maven.jar/jar-contents :added "1.1"}
(fact "lists the contents of a jar"
  
  (-> (java.io.File. *match-path*)
      (jar-contents))
  => (contains ["clojure/core/match.clj"] :in-any-order :gaps-ok))

^{:refer lucid.core.maven.jar/maven-file :added "1.1"}
(fact "returns the path of the local maven file"
  (maven-file ['org.clojure/core.match *match-version*])
  => *match-path*)

^{:refer lucid.core.maven.jar/find-all-jars :added "1.1"}
(fact "returns all jars within a repo in a form of a map"
  (-> (find-all-jars (str (fs/path "~/.m2/repository")))
      (get (str (fs/path "~/.m2/repository/org/clojure/core.match")))
      (get *match-version*))
  => *match-path*)

^{:refer lucid.core.maven.jar/find-latest-jars :added "1.1"}
(fact "returns the latest jars within a repo"
  (->> (find-latest-jars (str (fs/path "~/.m2/repository")))
       (filter #(= % *match-path*))
       first)
  => *match-path*)

^{:refer lucid.core.maven.jar/resolve-jar :added "1.1"}
(fact "resolves the path of a jar for a given namespace, according to many options"
  
  (resolve-jar 'clojure.core.match)
  => [*match-path* "clojure/core/match.clj"]

  ^:hidden
  (resolve-jar 'clojure.core.match :classloader file/*clojure-loader*)
  => [*match-path* "clojure/core/match.clj"]

  (resolve-jar 'clojure.core.match
               :jar-path
               *match-path*)
  => [*match-path* "clojure/core/match.clj"]

  (resolve-jar 'clojure.core.match
               :jar-paths
               [*match-path*])
  => [*match-path* "clojure/core/match.clj"]

  (resolve-jar 'clojure.core.match
               :coordinate
               ['org.clojure/core.match *match-version*])
  => [*match-path* "clojure/core/match.clj"]

  (resolve-jar 'clojure.core.match
               :coordinate
               ['im.chit/hara.io.file *match-version*])
  => nil

  (resolve-jar 'clojure.core.match
               :coordinates
               [['org.clojure/core.match *match-version*]])
  => [*match-path* "clojure/core/match.clj"]

  (resolve-jar 'clojure.core.match
               :coordinates
               [['org.clojure/core.match *match-version*]])

  (resolve-jar 'clojure.core.match :repository)
  => [*match-path* "clojure/core/match.clj"])
