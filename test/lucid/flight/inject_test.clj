(ns lucid.flight.inject-test
  (:use hara.test)
  (:require [lucid.flight.inject :refer :all]))

^{:refer lucid.flight.inject/inject-single :added "0.1"}
(fact "copies the var into a namespace with given symbol name"
  (inject-single 'user 'assoc2 #'clojure.core/assoc)
  => (var user/assoc2))

^{:refer lucid.flight.inject/inject-process-coll :added "0.1"}
(fact "takes a vector and formats it into a datastructure"
   (inject-process-coll '[clojure.core :refer :all])
   => '{:ns  clojure.core
        :op  :refer
        :arr :all}
   
  (inject-process-coll '[clojure.core :exclude [assoc]])
  => '{:ns  clojure.core
       :op  :exclude
       :arr [assoc]})

^{:refer lucid.flight.inject/inject-split-args :added "2.1"}
(fact "splits args for the inject namespace"
  (inject-split-args
  '[clojure.core >
    (clojure.pprint pprint)

    clojure.core
    (lucid.flight.reflection    .> .? .* .% .%>)

    a
    (clojure.repl apropos source doc find-doc
                  dir pst root-cause)
    (iroh.core [>ns ns] [>var var])
    (clojure.java.shell :refer [sh])
    (lucid.flight.inject :exclude [inject-single])
    (lucid.flight.pull :all)
    (lucid.flight.debug)])
  => '[{:ns clojure.core, :prefix >,
       :imports [{:ns clojure.pprint, :op :refer,
                  :arr (pprint)}]}
      {:ns clojure.core,
       :imports [{:ns lucid.flight.reflection, :op :refer,
                  :arr (.> .? .* .% .%>)}]}
      {:ns a,
       :imports [{:ns clojure.repl, :op :refer,
                  :arr (apropos source doc find-doc dir pst root-cause)}
                 {:ns iroh.core, :op :refer,
                  :arr ([>ns ns] [>var var])}
                 {:ns clojure.java.shell, :op :refer,
                  :arr [sh]}
                 {:ns lucid.flight.inject, :op :exclude,
                  :arr [inject-single]}
                 {:ns lucid.flight.pull, :op :exclude, :arr ()}
                 {:ns lucid.flight.debug, :op :exclude, :arr ()}]}])
