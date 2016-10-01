(ns lucid.core.code
  (:require [lucid.core.code
             [test :as test]
             [source :as source]]
            [rewrite-clj.node :as node]
            [hara.string.prose :as prose]
            [clojure.string :as string]))

(defn analyse-file
  ([path]
   (cond (.endsWith (str path) "_test.clj")
         (test/analyse-test-file path)

         :else
         (source/analyse-source-file path)))
  ([type path]
   (case type
     :source (source/analyse-source-file path)
     :test (test/analyse-test-file path))))
        
(defn join-nodes
  "treat test nodes specially when rendering code
 
   (->> (z/of-string \"(+ 1 1) => (+ 2 2)\")
        (iterate z/right*)
        (take-while identity)
        (map z/node)
        (process-doc-nodes))
   => \"(+ 1 1) => (+ 2 2)\"
   "
  {:added "1.2"}
  [docs]
  (->> docs
       (map (fn [node]
              (let [res (node/string node)]
                (cond (and (not (node/whitespace? node))
                           (not (node/comment? node))
                           (string? (node/value node)))
                      (prose/escape-newlines res)

                      :else res))))
       (string/join)))
