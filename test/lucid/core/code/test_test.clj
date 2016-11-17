(ns lucid.core.code.test-test
  (:use hara.test)
  (:require [lucid.core.code.test :refer :all]
            [lucid.core.code.test.common :as common]))

^{:refer lucid.core.code.test/find-frameworks :added "1.1"}
(fact "find test frameworks given a namespace form"
  (find-frameworks '(ns ...
                      (:use hara.test)))
  => #{:fact}

  (find-frameworks '(ns ...
                      (:use clojure.test)))
  => #{:clojure})

^{:refer lucid.core.code.test/analyse-test-file :added "1.1"}
(fact "analyses a test file for docstring forms"
  
  (-> (analyse-test-file "example/code.analysis/test/example/core_test.clj")
      (update-in '[example.core foo :test :code] common/join-nodes))
  => {'example.core
      {'foo {:test {:code "1\n  => 1",
                    :line {:row 6, :col 1, :end-row 7, :end-col 16},
                    :path "example/code.analysis/test/example/core_test.clj"},
             :meta {:added "0.1"},
             :intro ""}}})
