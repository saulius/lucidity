(ns lucid.core.debug-test
  (:use hara.test)
  (:require [lucid.core.debug :refer :all]
            [clojure.string :as string]))

^{:refer lucid.core.debug/wrap-print :added "1.1"}
(fact "wraps a function in a `println` statement'"

  ((lucid.core.debug/wrap-print + (quote (+ 1 2 3)) (quote ->)) 1 2 3)
  => 6

  (with-out-str
    ((lucid.core.debug/wrap-print + (quote (+ 1 2 3)) (quote ->)) 1 2 3))
  => "-> (+ 1 2 3) :: 6\n")


^{:refer lucid.core.debug/dbg-print :added "1.1"}
(fact "creates the form for debug print"

  (dbg-print '(+ 1 2 3) '->)
  => '((lucid.core.debug/wrap-print + (quote (+ 1 2 3)) (quote ->)) 1 2 3))

^{:refer lucid.core.debug/dbg-> :added "1.1"}
(fact "prints each stage of the `->` macro"
  (-> (dbg-> {:a 1}
                       (assoc :b 2)
                       (merge {:c 3}))
      (with-out-str)
      (string/split-lines))
  => ["" ""
      "{:a 1}"
      "-> (assoc :b 2) :: {:a 1, :b 2}"
      "-> (merge {:c 3}) :: {:a 1, :b 2, :c 3}"])

^{:refer lucid.core.debug/dbg->> :added "1.1"}
(fact "prints each stage of the `->>` macro"
  
  (->  (dbg->> (range 5)
               (map inc)
               (take 2))
      (with-out-str)
      (string/split-lines))
  => ["" ""
      "(0 1 2 3 4)"
      "->> (map inc) :: (1 2 3 4 5)"
      "->> (take 2) :: (1 2)"])
