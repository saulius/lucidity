(ns vinyasa.inject-test
  (:use lucid.sweet)
  (:require [vinyasa.inject :refer :all]))

(fact "inject-split args"
 (inject-split-args
  '[clojure.core >
    (clojure.pprint pprint)

    clojure.core
    (vinyasa.reflection    .> .? .* .% .%>)
    .
    (clojure.repl apropos source doc find-doc
                  dir pst root-cause)
    (iroh.core [>ns ns] [>var var])
    (clojure.java.shell :refer [sh])
    (vinyasa.inject :exclude [inject-single])
    (vinyasa.pull :all)
    (vinyasa.lein)])

 => '[{:ns clojure.core, :prefix >,
       :imports [{:ns clojure.pprint, :op :refer,
                  :arr (pprint)}]}
      {:ns clojure.core,
       :imports [{:ns vinyasa.reflection, :op :refer,
                  :arr (.> .? .* .% .%>)}]}
      {:ns .,
       :imports [{:ns clojure.repl, :op :refer,
                  :arr (apropos source doc find-doc dir pst root-cause)}
                 {:ns iroh.core, :op :refer,
                  :arr ([>ns ns] [>var var])}
                 {:ns clojure.java.shell, :op :refer,
                  :arr [sh]}
                 {:ns vinyasa.inject, :op :exclude,
                  :arr [inject-single]}
                 {:ns vinyasa.pull, :op :exclude, :arr ()}
                 {:ns vinyasa.lein, :op :exclude, :arr ()}]}])
