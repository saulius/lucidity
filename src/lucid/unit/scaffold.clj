(ns lucid.unit.scaffold
  (:require [lucid.core.code :as code]
            [lucid.unit.zipper :as zipper]
            [clojure.string :as string]
            [rewrite-clj.zip :as source]
            [rewrite-clj.node :as node]
            [hara.io.file :as fs]))

(defn test-ns-form
  "creates a test form for the namespace"
  {:added "1.2"}
  [ns]
  (format "(ns %s\n  (:use hara.test)\n  (:require [%s :refer :all]))"
          (str ns "-test")
          ns))

(defn test-fact-form
  "creates a fact form for the namespace"
  {:added "1.2"}
  [ns var version]
  (->> [(format "^{:refer %s/%s :added \"%s\"}"
                ns var version)
        (format "(fact \"TODO\")")]
       (string/join "\n")))

(defn scaffold-new
  "creates a completely new scaffold"
  {:added "1.2"}
  [ns vars test-ns version project]
  (let [test-file (format "%s/%s/%s"
                          (:root project)
                          (first (:test-paths project))
                          (-> test-ns str munge (.replaceAll "\\." "/") (str ".clj")))]
    (fs/create-directory (fs/parent test-file))
    (->> (map #(test-fact-form ns % version) vars)
         (cons (test-ns-form ns))
         (string/join "\n\n")
         (spit test-file))
    vars))

(defn scaffold-append
  "creates a scaffold for an already existing file"
  {:added "1.2"}
  [ns vars test-file version project]
  (let [test-vars (set (code/all-test-vars test-file))
        vars (remove test-vars vars)]
    (->> vars
         (map #(test-fact-form ns % version))
         (string/join "\n\n")
         (str "\n")
         (#(spit test-file % :append true)))
    vars))

(defn scaffold
  "creates a scaffold"
  {:added "1.2"}
  ([ns lookup project]
   (let [ns (code/source-namespace ns)
         test-ns   (symbol (str ns "-test"))
         src-file  (lookup ns)
         test-file (lookup test-ns)
         version (->> (string/split (:version project) #"\.")
                      (take 2)
                      (string/join "."))
         vars (if src-file (code/all-source-vars src-file))]
     (cond (empty? vars) (println "Namespace" ns "does not have any vars")

           (or (not test-file)
               (not (fs/exists? test-file)))
           (scaffold-new ns vars test-ns version project)
           
           :else
           (scaffold-append ns vars test-file version project)))))

(defn in-order?
  "checks if the test vars are in source order"
  {:added "1.2"}
  ([ns lookup project]
   (let [ns (code/source-namespace ns)
         test-ns   (symbol (str ns "-test"))
         src-file  (lookup ns)
         test-file (lookup test-ns)
         vars (if src-file (code/all-source-vars src-file))
         test-vars (if test-file (code/all-test-vars test-file))]
     (if (not= vars test-vars)
       [vars test-vars]
       true))))

(defn arrange
  "arranges tests to be in order"
  {:added "1.2"}
  ([ns lookup project]
   (let [ns (code/source-namespace ns)
         test-ns   (symbol (str ns "-test"))
         src-file  (lookup ns)
         test-file (lookup test-ns)
         vars       (if src-file (code/all-source-vars src-file))
         svars      (set vars)
         all-nodes  (if test-file
                      (->> (source/of-file test-file)
                           (iterate source/right)
                           (take-while identity)
                           (map source/node)))
         key-var    #(-> % :children first node/sexpr :refer name symbol svars)
         is-ns?     #(-> % node/sexpr first (= 'ns))
         is-var?    #(and (-> % node/tag (= :meta))
                          (key-var %))
         ns-nodes    (->> all-nodes (filter is-ns?))
         var-table   (->> all-nodes
                          (filter is-var?)
                          (map (juxt key-var identity))
                          (into {}))
         var-nodes   (keep var-table vars)
         other-nodes (->> all-nodes (remove is-ns?) (remove is-var?))]
     (if test-file
       (->> (concat ns-nodes var-nodes other-nodes)
            (map node/string)
            (string/join "\n\n")
            (spit test-file))))))
