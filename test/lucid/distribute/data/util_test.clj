(ns lucid.distribute.data.util-test
  (:use midje.sweet)
  (:require [lucid.distribute.data.util :refer :all]
            [clojure.java.io :as io]))

^{:refer lucid.distribute.data.util/interpret-dots :added "1.2"}
(fact "interprets dots within a path vector"

  (interpret-dots ["a" "b" "." "c"]) => ["a" "b" "c"]

  (interpret-dots ["a" "b" ".." "c"]) => ["a" "c"])

^{:refer lucid.distribute.data.util/drop-while-matching :added "1.2"}
(fact "drops elements from the head of the two sequences if they contain
   the same element"

  (drop-while-matching ["a" "b" "c"] ["a" "b" "d" "e"])
  => [["c"] ["d" "e"]])


^{:refer lucid.distribute.data.util/path-vector :added "1.2"}
(fact "creates a vector from a path"

  (path-vector "/usr/local/bin")
  => ["" "usr" "local" "bin"]

  (path-vector (io/file "/usr/local/bin"))
  => ["" "usr" "local" "bin"])

^{:refer lucid.distribute.data.util/relative-path :added "1.2"}
(fact "finds the relative path as a string of a particular file
  location given a base directory"

  (relative-path "/usr/local/bin" "/usr/local/bin/example/one.sh")
  => "example/one.sh"

  (relative-path "/usr/local/bin" "/usr/local/lib/clojure/all.clj")
  => "../lib/clojure/all.clj")

^{:refer lucid.distribute.data.util/best-match :added "1.2"}
(fact "groups a bunch of files using a map containing how they should
  be distributed based upon "

  (best-match "b/c/file.txt" {"a" #{"a" "b"}
                              "c" #{"b/c"}})
  => ["c" 2])

^{:refer lucid.distribute.data.util/group-by-distribution :added "1.2"}
(fact "groups a bunch of files using a map containing how they should
  be distributed based upon "

  (group-by-distribution {"a" #{"a" "b"} "c" #{"b/c"}}
                         ["a/file.txt" "b/c/file.txt" "b/file.txt" "none.txt"])
  => {nil #{"none.txt"}
      "a" #{"a/file.txt" "b/file.txt"}
      "c" #{"b/c/file.txt"}})
