(ns lucid.unit.scaffold-test
  (:use hara.test)
  (:require [lucid.unit.scaffold :refer :all]))

^{:refer lucid.unit.scaffold/test-ns-form :added "1.2"}
(fact "creates a test form for the namespace")

^{:refer lucid.unit.scaffold/test-fact-form :added "1.2"}
(fact "creates a fact form for the namespace")

^{:refer lucid.unit.scaffold/scaffold-new :added "1.2"}
(fact "creates a completely new scaffold")

^{:refer lucid.unit.scaffold/scaffold-append :added "1.2"}
(fact "creates a scaffold for an already existing file")

^{:refer lucid.unit.scaffold/scaffold :added "1.2"}
(fact "creates a scaffold")

^{:refer lucid.unit.scaffold/in-order? :added "1.2"}
(fact "checks if the test vars are in source order")

^{:refer lucid.unit.scaffold/arrange :added "1.2"}
(fact "arranges tests to be in order")
