(ns lucid.flight.inject)

(defn inject-single [to-ns sym svar]
  (intern to-ns
          (with-meta sym
            (select-keys (meta svar)
                         [:doc :macro :arglists]))
          (deref svar)))

(defn inject-process-coll [[ns & [arg & rest :as args]]]
  (let [[op arr] (cond (or (symbol? arg) (vector? arg))
                       [:refer args]

                       (= :refer arg)
                       [:refer (first rest)]

                       (or (nil? arg) (= :all arg))
                       [:exclude ()]

                       (= :exclude arg)
                       [:exclude (first rest)]

                       :else
                       (throw (Exception. "Cannot process input: " arg)))]
    {:ns ns
     :op op
     :arr arr}))

(defn inject-split-args [args]
  (let [{:keys [all current]}
        (reduce (fn [{:keys [last current all] :as m} arg]
                  (let [new-current (cond (symbol? arg)
                                          (cond (symbol? last)
                                                (assoc current :prefix arg)

                                                (nil? last)
                                                (assoc current :ns arg)

                                                (coll? last)
                                                {:ns arg :imports []})

                                          (coll? arg)
                                          (update-in current [:imports]
                                                     conj (inject-process-coll arg))
                                          :else
                                           (throw (Exception. (str arg " is not valid"))))
                         new-all    (if (and (symbol? arg) (coll? last))
                                      (conj all current)
                                      all)]
                     {:last arg :all new-all :current new-current}))
                {:last nil :current {:ns '. :imports []} :all []}
                args)]
    (conj all current)))

(defn inject-row-entry [to-ns prefix {:keys [op arr] from-ns :ns}]
  (require from-ns)
  (cond (= op :refer)
        (->> arr
             (map (fn [e]
                    (let [[from-sym to-sym]
                          (cond (vector? e) e

                                (symbol? e)
                                [e (symbol (str prefix e))]

                                :else (throw (Exception. (str e " has to be either a symbol or a vector."))))
                          from-full (symbol (str from-ns "/" from-sym))
                          from-var  (resolve from-full)]
                      (if from-var
                        [to-sym from-var]
                        (println "Warning: " from-full " cannot be resolved and will be ignored")))))
             (filter identity)
             (mapv #(apply inject-single to-ns %)))

        (= op :exclude)
        (mapv (fn [[sym svar]]
                (inject-single to-ns sym svar))
              (apply dissoc (ns-publics from-ns) arr))))

(defn inject-row [{:keys [imports prefix] to-ns :ns}]
  (create-ns to-ns)
  (mapcat #(inject-row-entry to-ns prefix %) imports)
  (if (= to-ns 'clojure.core)
    (refer-clojure)))

(defn inject [& args]
  (->> args
       (mapcat inject-split-args)
       (mapcat inject-row)
       vec))

(defmacro in [& args]
  (cons 'vector
        (mapcat inject-row
                (inject-split-args args))))
