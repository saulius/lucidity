(ns lucid.test.code.common-test
  (:use hara.test)
  (:require [lucid.test.code.common :refer :all]
            [rewrite-clj.zip :as z]))

^{:refer lucid.test.code.common/gather-meta :added "1.1"}
(fact "gets the metadata for a particular form"
  (-> (z/of-string "^{:refer clojure.core/+ :added \"1.1\"}\n(fact ...)")
      z/down z/right z/down
      gather-meta)
  => '{:added "1.1", :ns clojure.core, :var +, :refer clojure.core/+})

^{:refer lucid.test.code.common/gather-string :added "1.1"}
(fact "creates correctly spaced code string from normal docstring"
  
  (-> (z/of-string "\"hello\nworld\nalready\"")
      (gather-string)
      (str))
  => "\"hello\n  world\n  already\"")

^{:refer lucid.test.code.common/strip-quotes :added "1.1"}
(fact "takes away the quotes from a string for formatting purposes"

  (strip-quotes "\"hello\"")
  => "hello")
