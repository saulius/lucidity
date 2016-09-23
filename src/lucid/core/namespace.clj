(ns lucid.core.namespace)

(defn clear-aliases
  ([] (clear-aliases (.getName *ns*)))
  ([ns]
   (doseq [alias (keys (ns-aliases ns))]
     (ns-unalias ns alias))))

(defn clear-mappings
  ([] (clear-aliases (.getName *ns*)))
  ([ns]
   (doseq [alias (keys (ns-aliases ns))]
     (ns-unalias ns alias))))

