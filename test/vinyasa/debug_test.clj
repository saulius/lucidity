(ns vinyasa.debug-test
  (:use lucid.sweet)
  (:require [vinyasa.debug :refer :all]))

(fact "prints out the results of each call"
  (with-out-str
    (dbg->> 1 (+ 5) (+ 4 5 6)))
  => "\n\n1\n->> (+ 5) :: 6\n->> (+ 4 5 6) :: 21\n")

(comment
  (dbg->> [1 2 3 4] (map inc) (map dec))
  (macroexpand-1 '(dbg-> 1 (+ 5) (+ 4 5 6)))
  (clojure.core/-> 1 (nil 5) (nil 4 5 6)))
