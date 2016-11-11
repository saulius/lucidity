(ns lucid.distribute.split
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [lucid.package.file :refer [*sep*]]
            [lucid.distribute.util
              [rewrite :as rewrite]
              [sort :as sort]]
            [lucid.distribute
             [common :as common]
             [manifest :as manifest]]
            [hara.io
              [project :as project]
              [file :as fs]]))

(defn copy-files
  "copies a set of relative paths from one place to another"
  {:added "1.2"}
  [files source target]
  (reduce (fn [out f]
            (merge out
                   (fs/copy (str source *sep* f)
                            (str target *sep* f))))
          {}
          files))
          
(defn split-scaffold
  "generates the scaffold for the split path"
  {:added "1.2"}
  [project manifest]
  (let [interim (common/interim-path project)]
    (do (fs/create-directory interim)
        (fs/create-directory (str interim *sep* "root"))
        (fs/create-directory (str interim *sep* "branches")))
    (doseq [branch (-> manifest :branches keys)]
      (fs/create-directory (str interim *sep* "branches" *sep* branch)))))

(defn split-all-files
  "splits up the files in the manifest to various folders"
  {:added "1.2"}
  [project manifest]
  (let [interim (common/interim-path project)]
    (copy-files (-> manifest :root :files)
                (:root project)
                (str interim "/root"))
    (doseq [branch (-> manifest :branches keys)]
      (copy-files (-> manifest :branches (get branch) :files)
                  (:root project)
                  (str interim "/branches/" branch)))))
                  
(defn split-project-files
  "creates the necessary project files for deployment"
  {:added "1.2"}
  [project manifest]
  (spit (str (fs/path (common/interim-path project) "root" "project.clj"))
        (rewrite/root-project-string project manifest))
  (doseq [branch (-> manifest :branches keys)]
    (spit (str (fs/path (common/interim-path project) "branches" branch "project.clj"))
          (rewrite/branch-project-string project manifest branch))))

(defn clean
  "deletes the interim directory
          
   (clean (project/project))
   ;;=> deletes the `target/interim` directory"
  {:added "1.2"}
  ([]
   (clean (project/project)))
  ([project]
   (fs/delete (common/interim-path project))))

(defn split
  "splits up current project to put in the interim directory
          
   (split (project/project))
   ;;=> look in `target/interim` for changes
   "
  {:added "1.2"}
  ([]
   (split (project/project)))
  ([project]
   (split project (manifest/manifest project)))
  ([project manifest]
   (clean project)
   (let [packages (->> manifest sort/topsort-branch-deps
                       flatten
                       distinct
                       (map :id))]
     (println "\nAll Packages:" packages)
     (split-scaffold project manifest)
     (split-all-files project manifest)
     (split-project-files project manifest)
     packages)))
