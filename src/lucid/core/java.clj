(ns lucid.core.java
  (:require [hara.io.project :as project]
            [hara.io.file :as fs]
            [lucid.core.asm :as asm])
  (:import (java.util Arrays)
           (javax.tools ToolProvider
                        DiagnosticCollector
                        Diagnostic$Kind)))

(defn path->class
  "Creates a class symbol from a file
 
   (path->class \"test/Dog.java\")
   => 'test.Dog
 
   (path->class \"test/Cat.class\")
   => 'test.Cat"
  {:added "1.2"}
  [path]
  (-> (str path)
      (.replaceAll ".java" "")
      (.replaceAll ".class" "")
      (.replaceAll "/" ".")
      symbol))

(defn java-sources
  "lists source classes in a project
 
   (-> (java-sources (project/project))
       (keys)
       (sort))
   => '[test.Cat test.Dog test.DogBuilder
        test.Person test.PersonBuilder test.Pet]"
  {:added "1.2"}
  [{dirs :java-source-paths}]
  (->> (mapcat (fn [dir]
                 (->> (fs/select dir {:include [".java"]})
                      (map (juxt #(->> %
                                       (fs/relativize (fs/path dir))
                                       path->class)
                                 identity))))
               dirs)
       (into {})))

(defn javac-output
  "Shows output of compilation"
  {:added "1.2"}
  [collector]
  (doseq [d (.getDiagnostics collector)]
    (if (.getSource d)
      (println
       (format "%s: %s, line %d: %s\n"
               (.toString (.getKind d))
               (.. d getSource getName)
               (.getLineNumber d)
               (.getMessage d nil))))))

(defn javac
  "compiles classes using the built-in compiler
 
   (javac 'test.Cat 'test.Dog)
   ;;=> outputs `.class` files in target directory
   "
  {:added "1.2"}
  [& classes]
  (let [proj      (project/project)
        sources   (java-sources proj)
        compiler  (ToolProvider/getSystemJavaCompiler)
        collector (DiagnosticCollector.)
        manager   (.getStandardFileManager compiler collector nil nil)
        arr       (->> (keep sources classes)
                       (map #(.toFile %))
                       (into-array)
                       (Arrays/asList)
                       (.getJavaFileObjectsFromFiles manager))]
    (.call (.getTask compiler
                     *err*
                     manager
                     collector
                     (Arrays/asList (make-array String 0))
                     nil
                     arr))
    (javac-output collector)
    (->> (:java-source-paths proj)
         (map (fn [dir]
                (->> (fs/move dir "target/classes" {:include [".class$"]})
                     (reduce-kv (fn [total in target]
                                  (assoc total
                                         (path->class (fs/relativize dir in))
                                         target))
                                {}))))
         (apply merge))))

(defn reimport
  "compiles and reimports java source code dynamically
 
   (reimport 'test.Cat 'test.Dog)
   ;;=> (test.Cat test.Dog)"
  {:added "1.2"}
  [& classes]
  (for [[cls file] (apply javac classes)]
    (do (asm/unload-class (str cls))
        (asm/load-class file))))
