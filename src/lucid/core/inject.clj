(ns lucid.core.inject)

(defn inject-single
  "copies the var into a namespace with given symbol name
   (inject-single 'user 'assoc2 #'clojure.core/assoc)
   => #'user/assoc2"
  {:added "1.1"}
  [to-ns sym svar]
  (intern to-ns
          (with-meta sym
            (select-keys (meta svar)
                         [:doc :macro :arglists]))
          (deref svar)))

(defn inject-process-coll
  "takes a vector and formats it into a datastructure
    (inject-process-coll '[clojure.core :refer :all])
    => '{:ns  clojure.core
         :op  :refer
         :arr :all}
    
   (inject-process-coll '[clojure.core :exclude [assoc]])
   => '{:ns  clojure.core
        :op  :exclude
        :arr [assoc]}"
  {:added "1.1"}
  [[ns & [arg & rest :as args]]]
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

(defn inject-split-args
  "splits args for the inject namespace
   (inject-split-args
   '[clojure.core >
     (clojure.pprint pprint)
 
     clojure.core
     (lucid.reflection    .> .? .* .% .%>)
 
    a
     (clojure.repl apropos source doc find-doc
                   dir pst root-cause)
     (lucid.reflection [>ns ns] [>var var])
     (clojure.java.shell :refer [sh])
     (lucid.core.inject :exclude [inject-single])
     (lucid.package :all)
     (lucid.core.debug)])
   => '[{:ns clojure.core, :prefix >,
        :imports [{:ns clojure.pprint, :op :refer,
                   :arr (pprint)}]}
       {:ns clojure.core,
        :imports [{:ns lucid.reflection, :op :refer,
                   :arr (.> .? .* .% .%>)}]}
       {:ns a,
        :imports [{:ns clojure.repl, :op :refer,
                   :arr (apropos source doc find-doc dir pst root-cause)}
                  {:ns lucid.reflection, :op :refer,
                   :arr ([>ns ns] [>var var])}
                  {:ns clojure.java.shell, :op :refer,
                   :arr [sh]}
                  {:ns lucid.core.inject, :op :exclude,
                   :arr [inject-single]}
                  {:ns lucid.package, :op :exclude, :arr ()}
                  {:ns lucid.core.debug, :op :exclude, :arr ()}]}]"
  {:added "1.1"}
  [args]
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

(defn inject-row-entry
  "takes an entry and injects into a given namespace
   (inject-row-entry 'user
                     '-
                     '{:ns clojure.java.shell, :op :refer,
                       :arr [sh]})
   => [#'user/-sh]"
  {:added "1.1"}
  [to-ns prefix {:keys [op arr] from-ns :ns}]
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

(defn inject-row
  "takes an entry and injects into a given namespace
   (inject-row '{:ns a
                 :imports
                 [{:ns clojure.repl, :op :refer,
                   :arr [apropos source doc]}]})
   => [#'a/apropos #'a/source #'a/doc]"
  {:added "1.1"}
  [{:keys [imports prefix] to-ns :ns}]
  (let [_ (create-ns to-ns)
        out (mapcat #(inject-row-entry to-ns prefix %) imports)
        _   (if (= to-ns 'clojure.core)
              (refer-clojure))]
    out))

(defn inject
  "takes a list of injections and output the created vars
   (inject '[(clojure.repl apropos source doc)
 
             b
             (clojure.repl apropos source doc)])
   => [#'./apropos #'./source #'./doc #'b/apropos #'b/source #'b/doc]"
  {:added "1.1"}
  [& args]
  (->> args
       (mapcat inject-split-args)
       (mapcat inject-row)
       vec))

(defmacro in
  "takes a list of injections and output the created vars
   (in c (clojure.repl apropos source doc)
       d (clojure.repl apropos source doc)
       e (clojure.repl apropos source doc))
   => [#'c/apropos #'c/source #'c/doc
       #'d/apropos #'d/source #'d/doc
       #'e/apropos #'e/source #'e/doc]"
  {:added "1.1"}
  [& args]
  (cons 'vector
        (mapcat inject-row
                (inject-split-args args))))
