(ns vinyasa.graft
  (:require [iroh.core :refer [>ns]]
            [vinyasa.inject :as inject]
            [clojure.string :as string]))

(defn last-capitalized? [sym]
  (let [char (-> (str sym) (string/split #"\.") last first int)]
    (<= 65 char 90)))

(defn graft-namespace [from to]
  (let [old-ns *ns*
        all (keys (ns-publics from))]
    (eval `(ns ~to))
    (doseq [x all]
      (inject/inject-single to x (symbol (str from "/" x)) ))
    (in-ns (-> old-ns str symbol))))

(defn graft
  ([[from to]]
     (condp = (type from)
       Class (eval `(>ns ~to ~(-> from (.getName) symbol)))
       clojure.lang.Symbol (cond (last-capitalized? from)
                                 (eval `(>ns ~to ~from))

                                 :else
                                 (graft-namespace from to))))
  ([entry & more]
     (graft entry)
     (if more (apply graft more))))
