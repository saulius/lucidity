(ns lucid.unit.outline-test
  (:use hara.test)
  (:require [lucid.unit.outline :refer :all]))

^{:refer lucid.unit.outline/markdown-code? :added "1.2"}
(fact "checks if the string has the beginning/end of markdown code"

  (markdown-code? "```")
  => true
  
  (markdown-code? "```clojure" "clojure")
  => true)

^{:refer lucid.unit.outline/has-spacing? :added "1.2"}
(fact "checks that a string starts with `n` spaces"

  (has-spacing? "  hello" 2)
  => true)

^{:refer lucid.unit.outline/uncomment-arrows :added "1.2"}
(fact "uncomments the arrows for actual testing"

  (uncomment-arrows ";;=>")
  => "=>")

^{:refer lucid.unit.outline/lines :added "1.2"}
(fact "reads a file and numbers each line")

^{:refer lucid.unit.outline/filter-forms :added "1.2"}
(fact "grabs only clojure code from lines"
  (filter-forms  [[1 "hello there"]
                  [2 ""]
                  [3 "    (+ 1 2)"]
                  [4 "    => 3"]])
  => [[3 "(+ 1 2)"]
      [4 "=> 3"]])

^{:refer lucid.unit.outline/read-forms :added "1.2"}
(fact "reads clojure forms from lines"
  
  (read-forms  [[1 "hello there"]
                [2 ""]
                [3 "    (+ 1 2)"]
                [4 "    => 3"]])
  => '[(+ 1 2) => 3])

^{:refer lucid.unit.outline/fact-form :added "1.2"}
(fact "outputs a fact form"
  
  (fact-form "some_test.clj" ^{:line 2} '(+ 1 2) 3 )
  => (contains
      ['fact string? '(+ 1 2) '=> '3]))

^{:refer lucid.unit.outline/make-fact-forms :added "1.2"}
(fact "make fact form if there is an arrow"
  
  (make-fact-forms '[(+ 1 1) (+ 1 2) => 3] "some_test.clj")
  => (contains-in
      ['(+ 1 1)
       ['fact string? '(+ 1 2) '=> '3]]))

^{:refer lucid.unit.outline/test :added "1.2"}
(fact "tests a markdown file")

