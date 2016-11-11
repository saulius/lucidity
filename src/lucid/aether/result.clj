(ns lucid.aether.result
  (:require [hara.io.classpath :as classpath]
            [lucid.aether.artifact])
  (:import org.eclipse.aether.resolution.ArtifactDescriptorResult
           org.eclipse.aether.resolution.ArtifactResult
           org.eclipse.aether.collection.CollectResult
           org.eclipse.aether.resolution.DependencyResult
           org.eclipse.aether.deployment.DeployResult
           org.eclipse.aether.installation.InstallResult
           org.eclipse.aether.resolution.MetadataResult
           org.eclipse.aether.resolution.VersionRangeResult
           org.eclipse.aether.resolution.VersionResult
           org.eclipse.aether.graph.DependencyNode))

(defn dependency-graph
  "creates a dependency graph for the results"
  {:added "1.2"}
  ([node]
   (dependency-graph node
                     (fn [artifact]
                       (not (= (first (classpath/artifact :coord artifact))
                               'org.clojure/clojure)))))
  ([^DependencyNode node pred]
   (let [artifact (->> node
                       (.getArtifact)
                       (classpath/artifact :coord))
         children (-> (.getChildren node)
                      (filter (fn [child]
                                (-> child (.getArtifact) pred))))]
     (hash-map artifact (mapv dependency-graph children)))))

(defmulti summary
  "creates a summary for the different types of results"
  {:added "1.2"}
  type)

(defmethod summary DependencyResult
  [result]
  (dependency-graph (.getRoot result)))

(defmethod summary CollectResult
  [result]
  (dependency-graph (.getRoot result)))

(defmethod summary InstallResult
  [result]
  result)

(defmethod summary DeployResult
  [result]
  result)

