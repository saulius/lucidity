(ns lucid.aether.request
  (:require [hara.object :as object]
            [hara.io.classpath :as classpath]
            [lucid.aether artifact authentication local-repo remote-repo])
  (:import (org.eclipse.aether.graph Dependency
                                     DependencyNode
                                     DefaultDependencyNode)
           (org.eclipse.aether.metadata DefaultMetadata)
           (org.eclipse.aether.repository RemoteRepository)
           (org.eclipse.aether.collection CollectRequest)
           (org.eclipse.aether.deployment DeployRequest)
           (org.eclipse.aether.installation InstallRequest)
           (org.eclipse.aether.resolution ArtifactRequest
                                          DependencyRequest)))

(def artifact-map
  {:artifact
   {:type java.lang.Object
    :fn (fn [req artifact]
          (.setArtifact req (classpath/artifact :eclipse artifact)))}})

(def repositories-map
  {:repositories
   {:type java.util.List
    :fn (fn [req repositories]
          (->> repositories
               (mapv (fn [m]
                       (object/from-data m RemoteRepository)))
               (.setRepositories req)))}})

(def install-map
  {:artifacts
   {:type java.util.List
    :fn (fn [req artifacts]
          (->> artifacts
               (mapv (fn [x]
                       (classpath/artifact :eclipse x)))
               (.setArtifacts req)))}

   :metadata
   {:type java.util.List
    :fn (fn [req metadata]
          (->> metadata
               (mapv (fn [x]
                       (object/from-data metadata)))
               (.setMetadata req)))}})

(def collect-map
  {:root-artifact
   {:type java.lang.Object
    :fn (fn [req artifact]
          (.setRootArtifact req (classpath/artifact :eclipse artifact)))}

   :dependencies
   {:type java.util.List
    :fn (fn [req dependencies]
          (->> dependencies
               (mapv (fn [m]
                       (object/from-data m Dependency)))
               (.setDependencies req)))}
   :managed-dependencies
   {:type java.util.List
    :fn (fn [req dependencies]
          (->> dependencies
               (mapv (fn [m]
                       (object/from-data m Dependency)))
               (.setManagedDependencies req)))}})

(object/map-like

 Dependency
 {:tag "dep"
  :read :class
  :write {:construct {:fn (fn [artifact scope]
                            (Dependency. (classpath/artifact :eclipse artifact)
                                         (or scope "")))
                      :params [:artifact :scope]}
          :methods
          (-> (object/write-setters Dependency)
              (merge artifact-map))}}

 DependencyNode
 {:tag   "dep.node"
  :read  :class
  :write {:construct {:fn (fn [artifact]
                            (DefaultDependencyNode.
                             (classpath/artifact :eclipse artifact)))
                      :params [:artifact]}
          :methods
          (-> (object/write-setters DefaultDependencyNode)
              (merge artifact-map))}}
 
 DefaultDependencyNode
 {:tag   "dep.node"
  :read  :class
  :write {:construct {:fn (fn [artifact]
                            (DefaultDependencyNode.
                             (classpath/artifact :eclipse artifact)))
                      :params [:artifact]}
          :methods
          (-> (object/write-setters DefaultDependencyNode)
              (merge artifact-map))}})

(object/map-like

 ArtifactRequest
 {:tag "req.artifact"
  :read  :class
  :write {:methods
          (-> (object/write-setters ArtifactRequest)
              (merge artifact-map repositories-map))
          :empty (fn [m] (ArtifactRequest.))}}

 CollectRequest
 {:tag "req.collect"
  :read :class
  :write {:methods (-> (object/write-setters CollectRequest)
                       (merge root-artifact-map dependencies-map repositories-map))
          :empty (fn [m] (CollectRequest.))}}
 
 DependencyRequest
 {:tag "req.dependency"
  :read  {:to-map
          (fn [req]
            (object/to-data (.getCollectRequest req)))}
  :write {:from-map
          (fn [m]
            (doto (DependencyRequest.)
              (.setCollectRequest (object/from-data m CollectRequest))))}}

 DeployRequest
 {:tag "req.deploy"
  :read :class
  :write {:methods (-> (object/write-setters DeployRequest)
                       (merge install-map))
          :empty (fn [m] (DeployRequest.))}}
 
 InstallRequest
 {:tag "req.install"
  :read :class
  :write {:methods (-> (object/write-setters InstallRequest)
                       (merge install-map))
          :empty (fn [m] (InstallRequest.))}})

(defn flatten-values
  ""
  [node]
  (cond (map? node)
        (cons (key (first node))
                    (flatten-values (val (first node))))
        
        (vector? node)
        (mapcat (fn [item]
                  (cond (map? item)
                        (flatten-values item)

                        (vector? item)
                        [item]))
                node)))

(defn artifact-request
  "creates an `ArtifactRequest` object from map
 
   (artifact-request
    {:artifact \"im.chit:hara:2.4.8\"
     :repositories [{:id \"clojars\"
                     :authentication {:username \"zcaudate\"
                                      :password \"hello\"}
                     :url \"https://clojars.org/repo/\"}]})
   ;;=> #req.artifact{:artifact \"im.chit:hara:jar:2.4.8\",
   ;;                 :repositories [{:id \"clojars\",
   ;;                                 :url \"https://clojars.org/repo/\"
   ;;                                 :authentication {:username \"zcaudate\", :password \"hello\"}}],
   ;;                 :request-context \"\"}
   "
  {:added "1.2"}
  [{:keys [artifact repositories] :as m}]
  (object/from-data m ArtifactRequest))

(defn collect-request
  "creates a `CollectRequest` object from map
 
   (collect-request
    {:root {:artifact \"im.chit:hara:2.4.8\"}
     :repositories [{:id \"clojars\"
                     :url \"https://clojars.org/repo/\"}]})
   ;;=> #req.collect{:root {:artifact \"im.chit:hara:jar:2.4.8\",
   ;;                       :exclusions [],
   ;;                       :optional false,
   ;;                       :scope \"\",
   ;;                       :optional? false}
   ;;                :repositories [{:id \"clojars\",
   ;;                                :url \"https://clojars.org/repo/\"}]}
   "
  {:added "1.2"}
  [{:keys [root repositories] :as m}]
  (object/from-data m CollectRequest))

(defn dependency-request
  "creates a `DependencyRequest` object from map
 
   (dependency-request
    {:root {:artifact \"im.chit:hara:2.4.8\"}
     :repositories [{:id \"clojars\"
                     :url \"https://clojars.org/repo/\"}]})
   ;;=> #req.dependency{:root {:artifact \"im.chit:hara:jar:2.4.8\",
   ;;                          :exclusions [],
   ;;                          :optional false,
   ;;                          :scope \"\",
   ;;                          :optional? false}
   ;;                   :repositories [{:id \"clojars\",
   ;;                                   :url \"https://clojars.org/repo/\"}]}
   "
  {:added "1.2"}
  [{:keys [root repositories] :as m}]
  (object/from-data m DependencyRequest))

(defn deploy-request
  "creates a `DeployRequest` object from map
 
   (deploy-request
    {:artifacts [{:group \"im.chit\"
                  :artifact \"hara.string\"
                  :version \"2.4.8\"
                  :extension \"jar\"
                  :file \"hara-string.jar\"}]
     :repository {:id \"clojars\"
                  :url \"https://clojars.org/repo/\"
                  :authentication {:username \"zcaudate\"
                                   :password \"hello\"}}})
   ;;=> #req.deploy{:artifacts [\"im.chit:hara.string:jar:2.4.8\"]
  ;;               :repository {:id \"clojars\",
   ;;                            :authentication {:username \"zcaudate\", :password \"hello\"}
   ;;                            :url \"https://clojars.org/repo/\"}}
   "
  {:added "1.2"}
  [{:keys [artifacts repository] :as m}]
  (object/from-data m DeployRequest))

(defn install-request
  "creates a `InstallRequest` object from map
 
   (install-request
    {:artifacts [{:group \"im.chit\"
                  :artifact \"hara.string\"
                  :version \"2.4.8\"
                  :extension \"jar\"
                  :file \"hara-string.jar\"}
                 {:group \"im.chit\"
                  :artifact \"hara.string\"
                  :version \"2.4.8\"
                 :extension \"pom\"
                  :file \"hara-string.pom\"}]})
   ;;=> #req.install{:artifacts [\"im.chit:hara.string:jar:2.4.8\"
   ;;                            \"im.chit:hara.string:pom:2.4.8\"]
   ;;                :metadata []}
   "
  {:added "1.2"}
  [{:keys [artifacts] :as m}]
  (object/from-data m InstallRequest))
