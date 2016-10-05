(ns lucid.query.match.fn-test
  (:use hara.test)
  (:require  [lucid.query.match [pattern :refer :all] fn]))

^{:refer lucid.query.match.fn/pattern-fn :added "1.2"}
(fact "make sure that functions are working properly"
  ((pattern-fn vector?) [])
  => throws

  ((pattern-fn #'vector?) [])
  => true

  ((pattern-fn '^:% vector?) [])
  => true
  
  ((pattern-fn '^:% symbol?) [])
  => false

  ((pattern-fn '[^:% vector?]) [[]])
  => true)
