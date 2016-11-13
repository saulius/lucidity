(ns lucid.aether.artifact
  (:require [hara.io.classpath.artifact :as artifact]
            [hara.object :as object])
  (:import [org.eclipse.aether.artifact Artifact  DefaultArtifact]))

(defmethod artifact/rep Artifact
  [artifact]
  (artifact/->Rep (.getGroupId artifact)
                  (.getArtifactId artifact)
                  (.getExtension artifact)
                  (.getClassifier artifact)
                  (.getVersion artifact)
                  (.getProperties artifact)
                  (str (.getFile artifact))))

(defmethod artifact/artifact :eclipse
  [tag x]
  (let [{:keys [group artifact classifier
                extension version
                properties file]}
        (artifact/rep x)]
    (DefaultArtifact. group artifact classifier
                      extension version
                      properties (if file (java.io.File. file)))))

(object/map-like
 DefaultArtifact
 {:tag "artifact"
  :read {:to-string (fn [artifact]
                      (artifact/artifact :string artifact))
         :to-map (fn [artifact]
                   (into {} (artifact/rep artifact)))}
  :write {:from-map (fn [m]
                      (artifact/artifact :eclipse m))}})
