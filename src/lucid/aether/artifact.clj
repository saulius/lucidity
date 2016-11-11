(ns lucid.aether.artifact
  (:require [hara.io.classpath.artifact :as artifact])
  (:import [org.eclipse.aether.artifact Artifact  DefaultArtifact]))

(defmethod artifact/rep Artifact
  [artifact]
  (artifact/->Rep (.getGroupId artifact)
                  (.getArtifactId artifact)
                  (.getExtension artifact)
                  (.getClassifier artifact)
                  (.getVersion artifact)))

(defmethod artifact/artifact :eclipse
  [tag x]
  (DefaultArtifact. (artifact/artifact :string x)))

