(ns lucid.unit
  (:require [lucid.unit source test
             [common :as common]
             [zipper :as zipper]]
            [lucid.query :as query]
            [hara.io
             [file :as fs]
             [project :as project]]
            [clojure.string :as string]
            [rewrite-clj.zip :as source])
  (:refer-clojure :exclude [import]))

(defonce ^:dynamic *lookup* nil)

(defonce ^:dynamic *project* (project/project))

(defn refresh-project
  "refreshes `lucid.unit/*project*` whenever function is called"
  {:added "1.2"}
  []
  (alter-var-root #'*project*
                  (constantly (project/project))))

(defn refresh-lookup
  "refreshes `lucid.unit/*lookup*` whenever function is called"
  {:added "1.2"}
  []
  (let [files (project/all-files (concat (:source-paths *project*)
                                         (:test-paths *project*)))]
    (alter-var-root #'*lookup* (constantly files))))

(defn lookup-namespace
  "look up file associated with the namespace"
  {:added "1.2"}
  [ns]
  (let [res (get *lookup* ns)]
    (if (nil? res)
      (do (refresh-project)
          (refresh-lookup)
          (get *lookup* ns))
      res)))

(defn source-namespace
  "look up the source file, corresponding to the namespace"
  {:added "1.2"}
  [ns]
  (let [sns (if (instance? clojure.lang.Namespace ns)
              (str (.getName ns))
              (str ns))
        sns (if (.endsWith (str sns) "-test")
              (subs sns 0 (- (count sns) 5))    
              sns)]
    (symbol sns)))

(defn import
  "imports unit tests as docstrings"
  {:added "1.2"}
  ([] (import *ns*))
  ([ns]
   (cond (= ns :all)
         (doseq [ns (keys (project/all-files (:source-paths *project*)))]
           (import ns))
         
         :else
         (let [ns (source-namespace ns)
               src-file  (lookup-namespace ns)
               test-file (lookup-namespace (symbol (str ns "-test")))
               import-fn (fn [nsp refers]
                           (fn [zloc]
                             (zipper/insert zloc nsp refers)))
               refers (if test-file
                        (common/analyse-file :test test-file {})
                        {})]
           (if src-file
             (zipper/walk-file src-file nil refers import-fn))))))

(defn purge
  "purge docstrings and meta from file"
  {:added "1.2"}
  ([] (purge *ns*))
  ([ns]
   (cond (= ns :all)
         (doseq [ns (keys (project/all-files (:source-paths *project*)))]
           (purge ns))

         :else
         (let [ns (source-namespace ns)
               src-file (lookup-namespace ns)
               purge-fn (fn [nsp references] identity)]
           (if src-file
             (zipper/walk-file src-file nil nil purge-fn))))))

(defn missing
  "lists the functions that are missing unit tests"
  {:added "1.2"}
  ([] (missing *ns*))
  ([ns]
   (let [ns (source-namespace ns)
         src-file (lookup-namespace ns)
         test-file (lookup-namespace (symbol (str ns "-test")))
         src (if src-file
               (common/analyse-file :source src-file {}))
         test (if test-file
                (common/analyse-file :test test-file {}))]
     (keys (apply dissoc (get src ns) (keys (get test ns)))))))


(defn scaffold
  "builds the unit test scaffolding for the source"
  {:added "1.2"}
  ([] (scaffold *ns*))
  ([ns]
   (let [ns (source-namespace ns)
         test-ns   (symbol (str ns "-test"))
         src-file  (lookup-namespace ns)
         test-file (lookup-namespace test-ns)
         ns-form   (fn [ns]
                     (format "(ns %s\n  (:use hara.test)\n  (:require [%s :refer :all]))"
                             (str ns "-test")
                             ns))
         fact-form (fn [ns var version]
                     (->> [(format "^{:refer %s/%s :added \"%s\"}"
                                   ns var version)
                           (format "(fact \"TODO\")")]
                          (string/join "\n")))
         vars (if src-file
                (query/$ (source/of-file src-file)
                         [(#{defn defmacro defmulti} | _ ^:%?- string? ^:%?- map? & _)]
                         {:walk :top :return :sexpr}))
         version (->> (string/split (:version *project*) #"\.")
                      (take 2)
                      (string/join "."))]
     (if (and vars (or (not test-file)
                       (not (fs/exists? test-file))))
       (let [test-file (format "%s/%s"
                               (first (:test-paths *project*))
                               (->  test-ns str munge (.replaceAll "\\." "/") (str ".clj")))]
         (fs/create-directory (fs/parent test-file))
         (->> (map #(fact-form ns % version) vars)
              (cons (ns-form ns))
              (string/join "\n\n")
              (spit test-file)))))))
