(ns lucid.unit-test
  (:use hara.test)
  (:require [lucid.unit :refer :all]))

^{:refer lucid.unit/source-namespace :added "1.2"}
(fact "look up the source file, corresponding to the namespace"

  (source-namespace 'lucid.unit)
  => 'lucid.unit
  
  (source-namespace 'lucid.unit-test)
  => 'lucid.unit)

^{:refer lucid.unit/import :added "1.2"}
(comment "imports unit tests as docstrings"

  (import)
  ;; import docstrings for the current namespace

  (import 'lucid.unit)
  ;; import docstrings for a given namespace

  (import :all)
  ;; import docstrings for the entire project
  )

^{:refer lucid.unit/purge :added "1.2"}
(comment "purge docstrings and meta from file"
  
  (purge)
  ;; removes docstrings for the current namespace

  (purge 'lucid.unit)
  ;; removes docstrings for a given namespace

  (purge :all)
  ;; removes docstrings for the entire project
)

^{:refer lucid.unit/missing :added "1.2"}
(comment "checks functions that are missing in a given namespace"

  (missing)
  ;; lists missing functions for current namespace
  
  (missing 'lucid.unit)
  ;; lists missing functions for specific namespace
  )

^{:refer lucid.unit/scaffold :added "1.2"}
(fact "builds the unit test scaffolding for the source"

  (scaffold)
  ;; generates test scaffolding for current namespace
  
  (scaffold 'lucid.unit)
  ;; generates test scaffolding for specific namespace
  )
