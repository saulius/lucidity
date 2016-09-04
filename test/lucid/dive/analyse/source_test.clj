(ns lucid.dive.analyse.source-test
  (:use hara.test)
  (:require [lucid.dive.analyse.source :refer :all]))

^{:refer lucid.dive.analyse.source/analyse-source-file :added "1.1"}
(fact "analyses a source file for namespace and function definitions"
  (analyse-source-file "example/src/example/core.clj" {})
  => '{example.core
       {foo
        {:source "(defn foo\n  [x]\n  (println x \"Hello, World!\"))"}}})
