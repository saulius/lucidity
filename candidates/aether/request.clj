(ns lucid.aether.request
  (:require [lucid.aether.artifact :as artifact])
  (:import [org.eclipse.aether.collection CollectRequest]
           [org.eclipse.aether.deployment DeployRequest]
           [org.eclipse.aether.installation InstallRequest]
           [org.eclipse.aether.resolution ArtifactRequest DependencyRequest]
           [org.eclipse.aether.repository
            RemoteRepository
            RemoteRepository$Builder]
           [org.eclipse.aether.artifact DefaultArtifact]
           [org.eclipse.aether.util.artifact JavaScopes SubArtifact]
           [org.eclipse.aether.graph Dependency DependencyNode]))

(defn collect-request
  ""
  [coords]
  (doto (CollectRequest.)
    (.setRoot (Dependency. (artifact/artifact coords) JavaScopes/COMPILE))
    (.setRepositories
     [(-> (RemoteRepository$Builder.  "central" "default" "http://central.maven.org/maven2/")
          (.build))
      (-> (RemoteRepository$Builder.  "clojars" "default" "http://clojars.org/repo/")
          (.build))])))

(defn artifact-request
  ""
  [coords]
  (doto (ArtifactRequest.)
    (.setArtifact (artifact/artifact coords))
    (.setRepositories
     [(-> (RemoteRepository$Builder.  "central" "default" "http://central.maven.org/maven2/")
          (.build))
      (-> (RemoteRepository$Builder.  "clojars" "default" "http://clojars.org/repo/")
          (.build))])))

(defn dependency-request
  ""
  [coords]
  (doto (DependencyRequest.)
    (.setCollectRequest (collect-request coords))))

(defn install-request
  [coords {:keys [jar-file pom-file]}]
  (let [artifact (-> (artifact/artifact coords)
                     (.setFile (java.io.File. jar-file)))
        pom (-> (SubArtifact. artifact nil "pom")
                (.setFile (java.io.File. pom-file)))]
    (-> (InstallRequest.)
        (.setArtifacts [artifact pom]))))

(defn deploy-request
  [coords {:keys [jar-file pom-file]}]
  (let [artifact (-> (artifact/artifact coords)
                     (.setFile (java.io.File. jar-file)))
        pom (-> (SubArtifact. artifact nil "pom")
                (.setFile (java.io.File. pom-file)))]
    (-> (DeployRequest.)
        (.setArtifacts [artifact pom]))))


(comment
  (.getMetadata (install-request '[im.chit/hara.io.classloader "2.4.8"]
                                 {:jar-file "/Users/chris/Desktop/hara.io.classloader-2.4.8.jar"
                                  :pom-file "/Users/chris/Desktop/pom.xml"}))
  


  )
