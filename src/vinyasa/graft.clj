(ns vinyasa.graft
  (:require [iroh.core :as iroh]
            [vinyasa.inject :as inject]
            [clojure.string :as string]))

(defn last-capitalized? [sym]
  (let [char (-> (str sym) (string/split #"\.") last first int)]
    (<= 65 char 90)))

(defn graft-namespace [from to]
  (let [old-ns *ns*
        all (keys (ns-publics from))
        _   (eval `(ns ~to))
        res (doall
             (for [x all]
               (inject/inject-single to x (symbol (str from "/" x)) )))]
    (in-ns (-> old-ns str symbol))
    res))

(defn graft
  ([[from to]]
     (condp = (type from)
       Class (eval `(iroh/>ns ~to ~(-> from (.getName) symbol)))
       clojure.lang.Symbol (cond (last-capitalized? from)
                                 (eval `(iroh/>ns ~to ~from))

                                 :else
                                 (graft-namespace from to))))
  ([entry & more]
     (concat (graft entry)
            (if more (apply graft more) []))))
