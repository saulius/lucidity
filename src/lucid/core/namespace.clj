(ns lucid.core.namespace)

(defmacro run
  [func & args]
  (let [f (when-let [nsp (namespace func)]
            (require (symbol nsp))
            (resolve func))]
    (if f
      `(~func ~@args)
      :function-not-loaded)))

(defn clear-aliases
  "removes all namespace aliases
 
   (clear-aliases)
   
   ;; require clojure.string
   (require '[clojure.string :as string])
   => nil
 
   ;; error if a new namespace is set to the same alias
   (require '[clojure.set :as string])
   => (throws) ;;  Alias string already exists in namespace
 
   ;; clearing all aliases
   (clear-aliases)
 
   (ns-aliases *ns*)
   => {}
 
   ;; okay to require
   (require '[clojure.set :as string])
   => nil"
  {:added "1.2"}
  ([] (clear-aliases (.getName *ns*)))
  ([ns]
   (doseq [alias (keys (ns-aliases ns))]
     (ns-unalias ns alias))))

(defn clear-mappings
  "removes all mapped vars in the namespace
   
   ;; require `join`
   (require '[clojure.string :refer [join]])
     
   ;; check that it runs
   (join [\"a\" \"b\" \"c\"])
   => \"abc\"
     
   ;; clear mappings
   (clear-mappings)
   
   (clojure.core/refer-clojure)
   (use 'hara.tes)
   (require '[lucid.core.namespace :refer :all])
     
   ;; the mapped symbol is gone
 
   (eval '(join [\"a\" \"b\" \"c\"]))
   => (throws) ;; \"Unable to resolve symbol: join in this context\"
   "
  {:added "1.2"}
  ([] (clear-mappings (.getName *ns*)))
  ([ns]
   (doseq [func (keys (ns-map ns))]
     (ns-unmap ns func))))
