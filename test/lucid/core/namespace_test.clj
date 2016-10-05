(ns lucid.core.namespace-test
  (:use hara.test)
  (:require [lucid.core.namespace :refer :all]))

^{:refer lucid.core.namespace/clear-mappings :added "1.2"}
(fact "removes all mapped vars in the namespace"
  
  ;; require `join`
  (require '[clojure.string :refer [join]])
    
  ;; check that it runs
  (join ["a" "b" "c"])
  => "abc"
    
  ;; clear mappings
  (clear-mappings)
  
  (clojure.core/refer-clojure)
  (use 'hara.tes)
  (require '[lucid.core.namespace :refer :all])
    
  ;; the mapped symbol is gone

  (eval '(join ["a" "b" "c"]))
  => (throws) ;; "Unable to resolve symbol: join in this context"
  )

^{:refer lucid.core.namespace/clear-aliases :added "1.2"}
(fact "removes all namespace aliases"

  (clear-aliases)
  
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
