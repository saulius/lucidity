(ns lucid.core.code
  (:require [lucid.core.code
             [test :as test]
             [source :as source]]
            [rewrite-clj.node :as node]
            [hara.string.prose :as prose]
            [clojure.string :as string]))

(defn analyse-file
  "analyses a source or test file for information
 
   (analyse-file \"src/lucid/core/code.clj\")
   => (contains-in
       {'lucid.core.code
        {'analyse-file
         {:source {:code string?,
                   :line {:row number?
                          :col number?
                          :end-row number?
                          :end-col number?},
                   :path \"src/lucid/core/code.clj\"}}}})
 
   (analyse-file \"test/lucid/core/code_test.clj\")
   => (contains-in
       {'lucid.core.code
       {'analyse-file
         {:test {:code vector?
                 :line {:row number?
                          :col number?
                          :end-row number?
                          :end-col number?}
                 :path \"test/lucid/core/code_test.clj\"},
          :meta {:added \"1.2\"},
          :intro \"analyses a source or test file for information\"}}})"
  {:added "1.2"}
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
  "joins nodes together from a test
 
   (-> (analyse-file \"test/lucid/core/code_test.clj\")
       (get-in ['lucid.core.code 'analyse-file :test :code])
       (join-nodes))
   => string?"
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
