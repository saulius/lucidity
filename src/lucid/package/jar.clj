(ns lucid.package.jar
  (:require [lucid.package.pom :as pom]
            [hara.io
             [archive :as archive]
             [file :as fs]
             [project :as project]]))

(defn generate-manifest
  "creates a manifest.mf file for the project
 
   (generate-manifest (project/project))"
  {:added "1.2"}
  [project]
  (fs/create-directory (str (:root project) "/target/classes/META-INF"))
  (spit (str (:root project) "/target/classes/META-INF/MANIFEST.MF")
        (str "Manifest-Version: 1.0\n"
             "Built-By: lucid.package\n"
             "Created-By: lucid.package\n"
             "Build-Jdk: " (get (System/getProperties) "java.runtime.version")  "\n"
             "Main-Class: clojure.main\n")))

(defn generate-jar
  "creates a jar file for the project
 
   (generate-jar (project/project))"
  {:added "1.2"}
  [project]
  (let [jar-path (str (:root project)
                      "/target/"
                      (:artifact project)
                      "-"
                      (:version project) ".jar")
        _   (generate-manifest project)
        _   (fs/delete jar-path)
        jar (archive/open jar-path)]
    
    (archive/archive jar
                     (str (:root project) "/resources")
                     (fs/select (str (:root project) "/resources")
                                {:exclude [".DS_Store" fs/directory?]}))
    (archive/archive jar
                     (str (:root project) "/src")
                     (fs/select (str (:root project) "/src")
                                {:include [".clj$"]}))
    (archive/archive jar
                     (str (:root project) "/target/classes")
                     (fs/select (str (:root project) "/target/classes")
                                {:exclude [".DS_Store" fs/directory?]}))
    (archive/insert jar
                    "project.clj"
                    (str (:root project) "/project.clj"))
    (.close jar)
    jar-path))
