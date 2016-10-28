(ns lucid.distribute.manifest.classify
  (:require [hara.io.project :as project]
            [hara.io.file :as fs]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [lucid.space.file :as file :refer [*sep*]]))

(defn name->path [name]
  (-> (str name)
      (.replaceAll "\\." file/*sep*)
      (.replaceAll "-" "_")))

(defn submodule-file? [file root-dir base excludes]
  (let [base-path (name->path base)
        exclude-paths (map name->path excludes)
        file-path (.getAbsolutePath file)]
    (cond (some #(.startsWith file-path
                              (string/join *sep* [root-dir base-path %]))
                exclude-paths)
          false

          (.startsWith file-path
                       (string/join *sep* [root-dir base-path]))
          true

          :else false)))

(defn classify-file [file root-path base level]
  (let [base-path (name->path base)
        file-path (str (fs/path file))]
    (-> (subs file-path (+ 2 (count root-path) (count base-path)))
        (->> (re-find #"^(.*)\.cljs?$"))
        (second)
        (string/split (re-pattern *sep*))
        (->> (take level)
             (string/join ".")))))

(defn grab-namespaces [form]
  (when (or (= :use (first form))
            (= :require (first form)))
    (map (fn [x]
           (cond (symbol? x) x

                 (or (vector? x) (list? x))
                 (first x)))
         (next form))))

(defn grab-classes [form]
  (when (= :import (first form))
    (mapcat (fn [x]
              (cond (symbol? x) [x]

                    (or (vector? x) (list? x))
                    (map #(symbol (str (first x) "." %))
                         (rest x))))
            (next form))))

(defn read-file-namespace [file]
  (let [[_ ns & body] (read-string (slurp file))]
    {:ns ns
     :file file
     :dep-namespaces (vec (mapcat grab-namespaces body))
     :dep-classes (vec (mapcat grab-classes body))}))

(defn split-project-files
  ([project]
     (let [opts (:repack project)]
       (split-project-files
        (first (:source-paths project))
        (-> (or (:root opts)
                (:name project))
            (str)
            (.replaceAll "\\." *sep*))
        (or (:levels opts) 1)
        (or (:exclude opts) []))))

  ([root-dir base level excludes]
      (let [all-files (->> (fs/select root-dir {:include [".cljs?$"]})
                           (group-by #(submodule-file?
                                       % root-dir base excludes)))
            parent-files (vec (get all-files false))
            module-files (->> (get all-files true)
                              (group-by #(classify-file % root-dir base level)))]
        [parent-files module-files])))

(defn classify-modules [module-files]
  (->> module-files
       (map (fn [[k v]]
              (let [items (mapv read-file-namespace v)
                    namespaces (set (map :ns items))]
                [k {:package k
                    :namespaces namespaces
                    :dep-namespaces (clojure.set/difference
                                     (set (mapcat :dep-namespaces items))
                                     namespaces)
                    :dep-classes   (set (mapcat :dep-classes items))
                    :files (mapv (fn [x] (-> x :file (.getPath))) items)
                    :items (mapv (fn [item] (assoc item :file (-> item :file (.getPath)))) items)}])))
       (into {})))

(comment
  (submodule-file?
   (first (list-clojure-files "example/hara/src"))
   (.getAbsolutePath (io/file "example/hara/src/a/b/c"))
   "hara" []))
