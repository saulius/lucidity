(ns lucid.core.code.test.fact
  (:require [lucid.query :as query]
            [clojure.string :as string]
            [rewrite-clj.zip :as source]
            [rewrite-clj.node :as node]
            [lucid.core.code.test.common :as common]))

(defn gather-fact-body
  ""
  ([zloc]
     (gather-fact-body zloc []))
  ([zloc output]
     (cond (nil? zloc) output
           
           (and (= :meta (source/tag zloc))
                (= [:token :hidden] (source/value zloc)))
           output

           (query/match zloc string?)
           (recur (source/right* zloc)
                  (conj output (common/gather-string zloc)))
           
           :else
           (recur (source/right* zloc) (conj output (source/node zloc))))))

(defn gather-fact
  ""
  [zloc]
  (if-let [mta (common/gather-meta zloc)]
    (let [exp (source/sexpr zloc)
          [intro nzloc] (if (string? exp)
                          [exp (if (source/right zloc)
                                 (source/right* zloc))]
                          ["" zloc])]
      (assoc mta
             :line  (meta (source/node (source/up zloc)))
             :test  (if nzloc
                      (gather-fact-body nzloc)
                      [])
             :intro intro))))

(defmethod common/frameworks 'midje.sweet  [_]  :fact)
(defmethod common/frameworks 'hara.test    [_]  :fact)

(defmethod common/analyse-test :fact
  ([type zloc opts]
   (let [fns  (query/$ zloc [(fact | & _)] {:return :zipper :walk :top})]
     (->> (keep gather-fact fns)
          (reduce (fn [m {:keys [ns var test intro line] :as meta}]
                    (-> m
                        (update-in [ns var]
                                   assoc
                                   :test {:code test
                                          :line line
                                          :path (:path opts)}
                                   :meta (dissoc meta :test :line :intro :ns :var :refer)
                                   :intro intro)))
                  {})))))
