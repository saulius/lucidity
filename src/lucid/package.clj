(ns lucid.package
  (:require [hara.namespace.import :as ns]
            [hara.io.project :as project]
            [lucid.aether :as aether]
            [lucid.package
             [jar :as jar]
             [pom :as pom]
             [resolve :as resolve]
             ;;[user :as user]
             ]))

(ns/import
 
 lucid.package.pom
 [generate-pom]

 lucid.package.jar
 [generate-jar]

 lucid.package.resolve
 [list-dependencies
  pull
  resolve-with-dependencies])

(defn install [project]
  (let [jar-file (jar/generate-jar project)
        pom-file (pom/generate-pom project)]
    (-> project
        (select-keys [:group :artifact :version])
        (aether/install-artifact {:artifacts [{:file jar-file
                                               :extension "jar"}
                                              {:file pom-file
                                               :extension "pom"}]}))))

(defn deploy [project]
  (let [jar-file (jar/generate-jar project)
        pom-file (pom/generate-pom project)]
    (-> project
        (select-keys [:group :artifact :version])
        (aether/install-artifact {:artifacts [{:file jar-file
                                               :extension "jar"}
                                              {:file pom-file
                                               :extension "pom"}]}))))
