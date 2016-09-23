(ns lucid.unit.zipper-test
  (:use hara.test)
  (:require [lucid.unit.zipper :refer :all]
            [rewrite-clj.zip :as z]
            [rewrite-clj.node :as node]))

^{:refer lucid.unit.zipper/append-node :added "0.1"}
(fact "Adds node as well as whitespace and newline on right"

  (-> (z/of-string "(+)")
      (z/down)
      (append-node 2)
      (append-node 1)
      (z/->root-string))
  => "(+\n  1\n  2)")

^{:refer lucid.unit.zipper/has-quotes? :added "0.1"}
(fact "checks if a string has quotes"

  (has-quotes? "\"hello\"")
  => true)

^{:refer lucid.unit.zipper/strip-quotes :added "0.1"}
(fact "gets rid of quotes in a string"

  (strip-quotes "\"hello\"")
  => "hello")

^{:refer lucid.unit.zipper/escape-newlines :added "0.1"}
(fact "makes sure that newlines are printable"

  (escape-newlines "\\n")
  => "\\n")

^{:refer lucid.unit.zipper/escape-escapes :added "0.1"}
(fact "makes sure that newlines are printable"

  (escape-escapes "\\n")
  => "\\\\n")

^{:refer lucid.unit.zipper/escape-quotes :added "0.1"}
(fact "makes sure that quotes are printable in string form"

  (escape-quotes "\"hello\"")
  => "\\\"hello\\\"")

^{:refer lucid.unit.zipper/strip-quotes-array :added "0.1"}
(fact "utility that strips quotes when not the result of a fact"
  (strip-quotes-array ["\"hello\""])
  => ["hello"]
  
  (strip-quotes-array ["(str \"hello\")" " " "=>" " " "\"hello\""])
  => ["(str \"hello\")" " " "=>" " " "\"hello\""])


^{:refer lucid.unit.zipper/nodes->docstring :added "0.1"}
(fact "converts nodes to a docstring compatible"
  (->> (z/of-string "\"hello\"\n  (+ 1 2)\n => 3 ")
       (iterate z/right*)
       (take-while identity)
       (map z/node)
       (nodes->docstring)
       (node/string))
  => "\"hello\n   (+ 1 2)\n  => 3 \""

  (->> (z/of-string (str [\e \d]))
       (iterate z/right*)
       (take-while identity)
       (map z/node)
       (nodes->docstring)
       (str)
       (read-string))
  => "[\\e \\d]")

^{:refer lucid.unit.zipper/insert :added "0.1"}
(fact "inserts the meta information and docstring from tests")

^{:refer lucid.unit.zipper/write-file :added "0.1"}
(fact "exports the zipper contents to file")

^{:refer lucid.unit.zipper/selector :added "0.1"}
(fact "builds a selector for functions"

  (selector 'hello)
  => '[(#{defn defmacro defmulti} | hello ^:%?- string? ^:%?- map? & _)])

^{:refer lucid.unit.zipper/walk-file :added "0.1"}
(fact "helper function for file manipulation used by import and purge")
