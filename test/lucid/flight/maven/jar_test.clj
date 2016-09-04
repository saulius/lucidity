(ns lucid.flight.maven.jar-test
  (:use hara.test)
  (:require [lucid.flight.maven.jar :refer :all]
            [lucid.flight.maven.file :as file]
            [hara.io.file :as fs]
            [clojure.java.io :as io]))

(def ^:dynamic *hara-version* "2.4.1")

(def ^:dynamic *hara-test-path*
  (fs/path (str "~/.m2/repository/im/chit/hara.test/"
                *hara-version*
                "/hara.test-"
                *hara-version*
                ".jar")))

^{:refer lucid.flight.maven.jar/jar-file :added "1.1"}
(fact "returns a path as a jar or nil if it does not exist"

  (jar-file *hara-test-path*)
  => java.util.jar.JarFile)

^{:refer lucid.flight.maven.jar/jar-entry :added "1.1"}
(fact "returns an entry of the jar or nil if it does not exist"

  (jar-entry *hara-test-path* "hara/test.clj")
  => java.util.jar.JarFile$JarFileEntry

  (jar-entry *hara-test-path* "NON-FILE")
  => nil)

^{:refer lucid.flight.maven.jar/jar-stream :added "1.1"}
(fact "gets the input-stream of the entry for the jar"
  
  (-> (fs/file *hara-test-path*)
      (jar-stream "hara/test.clj")
      (fs/pushback)
      (read)
      second)
  => 'hara.test)

^{:refer lucid.flight.maven.jar/jar-contents :added "1.1"}
(fact "lists the contents of a jar"
  
  (-> (fs/file *hara-test-path*)
      (jar-contents))
  => (contains ["project.clj" "hara/test.clj"] :in-any-order :gaps-ok))

^{:refer lucid.flight.maven.jar/maven-file :added "1.1"}
(fact "returns the path of the local maven file"
  (maven-file ['im.chit/hara.test *hara-version*])
  => *hara-test-path*)

^{:refer lucid.flight.maven.jar/find-all-jars :added "1.1"}
(fact "returns all jars within a repo in a form of a map"
  (-> (find-all-jars (fs/path "~/.m2/repository"))
      (get (fs/path "~/.m2/repository/im/chit/hara.test"))
      (get *hara-version*))
  => *hara-test-path*)

^{:refer lucid.flight.maven.jar/find-latest-jars :added "1.1"}
(fact "returns the latest jars within a repo"
  (->> (find-latest-jars (fs/path "~/.m2/repository"))
       (filter #(= % *hara-test-path*))
       first)
  => *hara-test-path*)

^{:refer lucid.flight.maven.jar/resolve-jar :added "1.1"}
(fact "resolves the path of a jar for a given namespace, according to many options"
  
  (resolve-jar 'hara.test)
  => [*hara-test-path* "hara/test.clj"]

  ^:hidden
  (resolve-jar 'hara.test :classloader file/*clojure-loader*)
  => [*hara-test-path* "hara/test.clj"]

  (resolve-jar 'hara.test
               :jar-path
               *hara-test-path*)
  => [*hara-test-path* "hara/test.clj"]

  (resolve-jar 'hara.test
               :jar-paths
               [*hara-test-path*])
  => [*hara-test-path* "hara/test.clj"]

  (resolve-jar 'hara.test
               :coordinate
               ['im.chit/hara.test *hara-version*])
  => [*hara-test-path* "hara/test.clj"]

  (resolve-jar 'hara.test
               :coordinate
               ['im.chit/hara.io.file *hara-version*])
  => nil

  (resolve-jar 'hara.test
               :coordinates
               [['im.chit/hara.test *hara-version*]])
  => [*hara-test-path* "hara/test.clj"]

  (resolve-jar 'hara.test
               :coordinates
               [['im.chit/hara.test *hara-version*]])

  (resolve-jar 'hara.test :repository)
  => [*hara-test-path* "hara/test.clj"])
