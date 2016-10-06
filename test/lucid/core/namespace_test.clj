(ns lucid.core.namespace-test
  (:use hara.test)
  (:require [lucid.core.namespace :refer :all :exclude [run] :as ns]))

^{:refer lucid.core.namespace/run :added "1.2"}
(fact "runs a function, automatically loading it if not loaded"
  
  (ns/run clojure.core/apply + 1 [2 3 4])
  => 10

  (ns/run wrong-function + 1 [2 3 4])
  => :function-not-loaded)

^{:refer lucid.core.namespace/clear-mappings :added "1.2"}
(comment "removes all mapped vars in the namespace"
  
  ;; require `join`
  (require '[clojure.string :refer [join]])
    
  ;; check that it runs
  (join ["a" "b" "c"])
  => "abc"
    
  ;; clear mappings
  (clear-mappings)
  
  ;; the mapped symbol is gone
  (join ["a" "b" "c"])
  => (throws) ;; "Unable to resolve symbol: join in this context"
  )

^{:refer lucid.core.namespace/clear-aliases :added "1.2"}
(comment "removes all namespace aliases"

  ;; require clojure.string
  (require '[clojure.string :as string])
  => nil

  ;; error if a new namespace is set to the same alias
  (require '[clojure.set :as string])
  => (throws) ;;  Alias string already exists in namespace

  ;; clearing all aliases
  (clear-aliases)

  (ns-aliases *ns*)
  => {}

  ;; okay to require
  (require '[clojure.set :as string])
  => nil)
