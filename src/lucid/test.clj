(ns lucid.test
  (:require [lucid.core
             [code :as code]]
            [lucid.test
             [zipper :as zipper]]
            [lucid.query :as query]
            [hara.io
             [file :as fs]
             [project :as project]]
            [clojure.string :as string]
            [rewrite-clj.zip :as source])
  (:refer-clojure :exclude [import]))

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
  ([ns] (import ns (project/project)))
  ([ns project] (import ns (project/file-lookup project) project))
  ([ns lookup project]
   (cond (= ns :all)
         (doseq [ns (keys (project/all-files (:source-paths project) {} project))]
           (import ns lookup project))
         
         :else
         (let [ns (source-namespace ns)
               src-file  (lookup ns)
               test-file (lookup (symbol (str ns "-test")))
               import-fn (fn [nsp refers]
                           (fn [zloc]
                             (zipper/insert zloc nsp refers)))
               refers (if test-file
                        (code/analyse-file :test test-file)
                        {})]
           (if src-file
             (zipper/walk-file src-file nil refers import-fn))))))

(defn purge
  "purge docstrings and meta from file"
  {:added "1.2"}
  ([] (purge *ns*))
  ([ns] (purge ns (project/project)))
  ([ns project] (purge ns (project/file-lookup project) project))
  ([ns lookup project]
   (cond (= ns :all)
         (doseq [ns (keys (project/all-files (:source-paths project) {} project))]
           (purge ns lookup project))

         :else
         (let [ns (source-namespace ns)
               src-file (lookup ns)
               purge-fn (fn [nsp references] identity)]
           (if src-file
             (zipper/walk-file src-file nil nil purge-fn))))))

(defn missing
  "lists the functions that are missing unit tests"
  {:added "1.2"}
  ([] (missing *ns*))
  ([ns] (missing ns (project/project)))
  ([ns project] (missing ns (project/file-lookup project) project))
  ([ns lookup project]
   (let [ns (source-namespace ns)
         src-file (lookup ns)
         test-file (lookup (symbol (str ns "-test")))
         src (if src-file
               (code/analyse-file :source src-file))
         test (if test-file
                (code/analyse-file :test test-file))]
     (keys (apply dissoc (get src ns) (keys (get test ns)))))))

(defn scaffold
  "builds the unit test scaffolding for the source"
  {:added "1.2"}
  ([] (scaffold *ns*))
  ([ns] (scaffold ns (project/project)))
  ([ns project] (scaffold ns (project/file-lookup project) project))
  ([ns lookup project]
   (let [ns (source-namespace ns)
         test-ns   (symbol (str ns "-test"))
         src-file  (lookup ns)
         test-file (lookup test-ns)
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
         version (->> (string/split (:version project) #"\.")
                      (take 2)
                      (string/join "."))]
     (if (and vars (or (not test-file)
                       (not (fs/exists? test-file))))
       (let [test-file (format "%s/%s/%s"
                               (:root project)
                               (first (:test-paths project))
                               (->  test-ns str munge (.replaceAll "\\." "/") (str ".clj")))]
         (fs/create-directory (fs/parent test-file))
         (->> (map #(fact-form ns % version) vars)
              (cons (ns-form ns))
              (string/join "\n\n")
              (spit test-file)))))))
