(ns lucid.publish.engine
  (:require [hara.class.multi :as multi]
            [hara.class.checks :as checks])
  (:import clojure.lang.MultiFn))

(defn wrap-hidden [f]
  (fn [{:keys [hidden] :as elem}]
    (if-not hidden (f elem))))

(defn engine [name]
  (let [ns (cond (string? name)
                 (symbol (str "lucid.publish.engine." name))
                 
                 (symbol? name) name
                 
                 :else (throw (Exception.
                               (format "Not string or symbol: %s" name))))]
    (require ns)
    (reduce-kv (fn [out k ref]
                 (let [v @ref]
                   (cond (instance? MultiFn  v)
                         (assoc out (keyword k)
                                (wrap-hidden (multi/multimethod v (str k))))

                         (fn? v)
                         (assoc out (keyword v) v)

                         :else out)))
                {}
                (ns-interns ns))))
