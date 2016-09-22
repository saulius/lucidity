(ns lucid.wake.analyse.test
    (:require [lucid.query :as query]
            [rewrite-clj.zip :as source]
            [rewrite-clj.node :as node]
            [clojure.walk :as walk]
            [hara.data.nested :as nested]
            [lucid.wake.analyse.common :as common]
            [lucid.wake.analyse.test
             [common :as test] clojure fact]))

(defn find-frameworks
  "find test frameworks given a namespace form
   (find-frameworks '(ns ...
                       (:use hara.test)))
   => #{:fact}
 
   (find-frameworks '(ns ...
                       (:use clojure.test)))
   => #{:clojure}"
  {:added "1.1"}
  [ns-form]
  (let [folio (atom #{})]
    (walk/postwalk (fn [form]
                     (if-let [k (test/frameworks form)]
                       (swap! folio conj k)))
                   ns-form)
    @folio))

(defn analyse-test-file
  "analyses a test file for docstring forms
   (-> (analyse-test-file \"example/test/example/core_test.clj\" {})
       (update-in '[example.core foo :docs] common/join-nodes))
   => '{example.core {foo {:docs \"1\\n  => 1\", :meta {:added \"0.1\"}}}}"
  {:added "1.1"}
  [file opts]
  (let [zloc   (source/of-file file)
        nsloc  (query/$ zloc [(ns | _ & _)] {:walk :top
                                             :return :zipper
                                             :first true})
        nsp        (source/sexpr nsloc)
        ns-form    (-> nsloc source/up source/sexpr)
        frameworks (find-frameworks ns-form)]
    (->> frameworks
         (map (fn [framework]
                (test/analyse-test framework zloc)))
         (apply nested/merge-nested))))

(defmethod common/analyse-file
  :test
  [_ file opts]
  (analyse-test-file file opts))
