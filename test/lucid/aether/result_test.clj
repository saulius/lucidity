(ns lucid.aether.result-test
  (:use hara.test)
  (:require [lucid.aether.result :refer :all]))

^{:refer lucid.aether.result/dependency-graph :added "1.2"}
(fact "creates a dependency graph for the results")

^{:refer lucid.aether.result/summary :added "1.2"}
(fact "creates a summary for the different types of results")
