(ns lucid.core.namespace)

(defn clear-aliases
  ([] (clear-aliases (.getName *ns*)))
  ([ns]
   (doseq [alias (keys (ns-aliases ns))]
     (ns-unalias ns alias))))

(defn clear-mappings
  ([] (clear-mappings (.getName *ns*)))
  ([ns]
   (doseq [func (keys (ns-interns ns))]
     (ns-unmap ns func))))

(comment (ns-interns *ns*)
         (keys (ns-interns 'lucid.core.namespace))
         (ns-unmap 'lucid.core.namespace 'clear-mappings)
         (clear-mappings)
         (ns-imports *ns*)
         (ns-interns *ns*))
