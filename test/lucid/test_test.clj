(ns lucid.test-test
  (:use hara.test)
  (:require [lucid.test :refer :all]))


^{:refer lucid.test/source-namespace :added "1.2"}
(fact "look up the source file, corresponding to the namespace")

^{:refer lucid.test/import :added "1.2"}
(fact "imports unit tests as docstrings")

^{:refer lucid.test/purge :added "1.2"}
(fact "purge docstrings and meta from file")

^{:refer lucid.test/missing :added "1.2"}
(fact "lists the functions that are missing unit tests")

^{:refer lucid.test/scaffold :added "1.2"}
(fact "builds the unit test scaffolding for the source")

(comment
  (lucid.test/import)
  )
