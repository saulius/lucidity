(ns lucid.unit.source-test
  (:use hara.test)
  (:require [lucid.unit.source :refer :all]))

^{:refer lucid.unit.source/analyse-source-file :added "1.1"}
(fact "analyses a source file for namespace and function definitions"
  (analyse-source-file "example/src/example/core.clj" {})
  => '{example.core
       {foo
        {:source "(defn foo\n  [x]\n  (println x \"Hello, World!\"))"}}})
