(ns lucid.dive.analyse.test-test
  (:use hara.test)
  (:require [lucid.dive.analyse.test :refer :all]
            [lucid.dive.analyse.test.common :as common]))

^{:refer lucid.dive.analyse.test/find-frameworks :added "1.1"}
(fact "find test frameworks given a namespace form"
  (find-frameworks '(ns ...
                      (:use hara.test)))
  => #{:fact}

  (find-frameworks '(ns ...
                      (:use clojure.test)))
  => #{:clojure})

^{:refer lucid.dive.analyse.test/analyse-test-file :added "1.1"}
(fact "analyses a test file for docstring forms"
  
  (-> (analyse-test-file "example/test/example/core_test.clj" {})
      (update-in '[example.core foo :docs] common/join-nodes))
  => {'example.core {'foo {:docs "1\n  => 1", :meta {:added "0.1"}}}})
