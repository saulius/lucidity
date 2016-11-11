(ns lucid.distribute
  (:require [hara.namespace.import :as ns]
            [lucid.core.package :as package]
            [lucid.distribute
             [common :as common]
             [manifest :as manifest]
             [split :as split]]
            [hara.io
             [file :as fs]
             [project :as project]]))

(ns/import lucid.distribute.manifest [manifest]
           lucid.distribute.split [clean split])

(defn install
  ([] (install (project/project)))
  ([project]
   (install project (manifest/manifest project)))
  ([project manifest]
   (let [packages (split/split project manifest)]
     (doseq [id packages]
       (println "\nInstalling" id)
       (-> (common/interim-path project)
           (str "/branches/" id "/project.clj")
           (project/project)
           (package/install)))
     
     (println "\nInstalling Root")
     (-> (common/interim-path project)
         (str "/root/project.clj")
         (project/project)
         (package/install)))))

(defn deploy
  ([] (deploy (project/project)))
  ([project]
   (deploy project (manifest/manifest project)))
  ([project manifest]
   (let [packages (split/split project manifest)]
     (doseq [id packages]
       (println "\nDeploying" id)
       (-> (common/interim-path project)
           (str "/branches/" id "/project.clj")
           (project/project)
           (package/deploy)))
     
     (println "\nDeploying Root")
     (-> (common/interim-path project)
         (str "/root/project.clj")
         (project/project)
         (package/deploy)))))


(comment
  (def mani (manifest/manifest (project/project "../hara/project.clj")))
  (split/split (project/project "../hara/project.clj"))
  
  )
