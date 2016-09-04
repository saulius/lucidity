(ns lucid.dive.analyse.test.fact-test
  (:use hara.test)
  (:require [lucid.dive.analyse.test.fact :refer :all]
            [lucid.dive.analyse.test.common :as common]
            [rewrite-clj.zip :as z]))

^{:refer lucid.dive.analyse.test.midje/gather-fact :added "1.1"}
(fact "Make docstring notation out of fact form"
  (-> "^{:refer example/hello-world :added \"0.1\"}
       (fact \"Sample test program\"\n  (+ 1 1) => 2\n  (long? 3) => true)"
      (z/of-string)
      z/down z/right z/down z/right
      (gather-fact)
      :docs
      common/join-nodes)
  => "\"Sample test program\"\n  (+ 1 1) => 2\n  (long? 3) => true")
