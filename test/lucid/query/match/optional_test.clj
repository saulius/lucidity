(ns lucid.query.match.optional-test
  (:use hara.test)
  (:require [lucid.query.match.optional :refer :all]))

^{:refer lucid.query.match.optional/pattern-seq :added "0.2"}
(fact "generate a sequence of possible matches"
  (pattern-seq '(+ ^:? (1) ^:? (^:? + 2)))
  => '((+)
       (+ (1))
       (+ (2))
       (+ (1) (2))
       (+ (+ 2))
       (+ (1) (+ 2))))


