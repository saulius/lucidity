(ns lucid.core.inject-test
  (:use hara.test)
  (:require [lucid.core.inject :refer :all]))

^{:refer lucid.core.inject/inject-single :added "1.1"}
(fact "copies the var into a namespace with given symbol name"
  (inject-single 'user 'assoc2 #'clojure.core/assoc)
  => #'user/assoc2)

^{:refer lucid.core.inject/inject-process-coll :added "1.1"}
(fact "takes a vector and formats it into a datastructure"
   (inject-process-coll '[clojure.core :refer :all])
   => '{:ns  clojure.core
        :op  :refer
        :arr :all}
   
  (inject-process-coll '[clojure.core :exclude [assoc]])
  => '{:ns  clojure.core
       :op  :exclude
       :arr [assoc]})

^{:refer lucid.core.inject/inject-split-args :added "1.1"}
(fact "splits args for the inject namespace"
  (inject-split-args
  '[clojure.core >
    (clojure.pprint pprint)

    clojure.core
    (lucid.reflection    .> .? .* .% .%>)

    a
    (clojure.repl apropos source doc find-doc
                  dir pst root-cause)
    (lucid.reflection [>ns ns] [>var var])
    (clojure.java.shell :refer [sh])
    (lucid.core.inject :exclude [inject-single])
    (lucid.space :all)
    (lucid.core.debug)])
  => '[{:ns clojure.core, :prefix >,
       :imports [{:ns clojure.pprint, :op :refer,
                  :arr (pprint)}]}
      {:ns clojure.core,
       :imports [{:ns lucid.reflection, :op :refer,
                  :arr (.> .? .* .% .%>)}]}
      {:ns a,
       :imports [{:ns clojure.repl, :op :refer,
                  :arr (apropos source doc find-doc dir pst root-cause)}
                 {:ns lucid.reflection, :op :refer,
                  :arr ([>ns ns] [>var var])}
                 {:ns clojure.java.shell, :op :refer,
                  :arr [sh]}
                 {:ns lucid.core.inject, :op :exclude,
                  :arr [inject-single]}
                 {:ns lucid.space, :op :exclude, :arr ()}
                 {:ns lucid.core.debug, :op :exclude, :arr ()}]}])

^{:refer lucid.core.inject/inject-row-entry :added "1.1"}
(fact "takes an entry and injects into a given namespace"
  (inject-row-entry 'user
                    '-
                    '{:ns clojure.java.shell, :op :refer,
                      :arr [sh]})
  => [#'user/-sh])

^{:refer lucid.core.inject/inject-row :added "1.1"}
(fact "takes an entry and injects into a given namespace"
  (inject-row '{:ns a
                :imports
                [{:ns clojure.repl, :op :refer,
                  :arr [apropos source doc]}]})
  => [#'a/apropos #'a/source #'a/doc])

^{:refer lucid.core.inject/inject :added "1.1"}
(fact "takes a list of injections and output the created vars"
  (inject '[(clojure.repl apropos source doc)

            b
            (clojure.repl apropos source doc)])
  => [#'./apropos #'./source #'./doc #'b/apropos #'b/source #'b/doc])

^{:refer lucid.core.inject/in :added "1.1"}
(fact "takes a list of injections and output the created vars"
  (in c (clojure.repl apropos source doc)
      d (clojure.repl apropos source doc)
      e (clojure.repl apropos source doc))
  => [#'c/apropos #'c/source #'c/doc
      #'d/apropos #'d/source #'d/doc
      #'e/apropos #'e/source #'e/doc])
