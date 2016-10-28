(ns lucid.distribute.manifest.graph.internal
  (:require [clojure.set :as set]))

(defn find-module-dependencies
  "finds internal module dependencies
 
   (def tally
     (reduce-kv (fn [i k v]
                  (assoc i k {:imports (apply set/union (map :imports v))
                              :exports (apply set/union (map :exports v))}))
                {}
                *files*))
 
   (find-module-dependencies
    \"web\"
   '{:imports #{[:clj repack.util.array]
                 [:cljs repack.web.client]
                 [:class im.chit.repack.common.Hello]
                 [:clj repack.core]},
      :exports #{[:clj repack.web]
                 [:cljs nil]
                 [:class repack.web.client.Main]
                 [:class repack.web.client.Client]
                 [:clj repack.web.client]
                 [:cljs repack.web]
                 [:class im.chit.repack.web.Client]}}
    tally)
   => #{\"core\" \"util.array\" \"common\"}"
  {:added "1.2"}
  [sym symv tally]
  (reduce-kv (fn [i k v]
               (if (empty? (set/intersection (:imports symv) (:exports v)))
                 i
                 (conj i k)))
             #{}
             (dissoc tally sym)))

(defn find-all-module-dependencies
  "finds all internal module dependencies through analysis of :imports and :exports
 
  (find-all-module-dependencies *files*)
   => {\"resources\" #{},
       \"jvm\" #{},
       \"common\" #{},
       \"core\" #{},
       \"util.array\" #{},
      \"util.data\" #{\"util.array\"},
       \"web\" #{\"core\" \"common\" \"util.array\"}}"
  {:added "1.2"}
  [filemap]
  (let [tally (reduce-kv (fn [i k v]
                           (assoc i k {:imports (apply set/union (map :imports v))
                                       :exports (apply set/union (map :exports v))}))
                         {}
                         filemap)]
    (reduce-kv (fn [i k v]
                 (assoc i k (find-module-dependencies k v tally)))
               {}
               tally)))

(defn resource-dependencies
  "looks at the config to see if there are any explicitly stated dependencies.
 
   (resource-dependencies *config*)
   => {\"core\" #{\"resources\"}}"
  {:added "1.2"}
  [cfgs]
  (->> (filter #(and (:subpackage %)
                     (:dependents %)) cfgs)
       (map (fn [{pkg  :subpackage
                 deps :dependents}]
              (zipmap deps (repeat #{pkg}))))
       (apply merge-with set/union)))
