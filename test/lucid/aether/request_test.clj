(ns lucid.aether.request-test
  (:use hara.test)
  (:require [lucid.aether.request :refer :all]
            [hara.object :as object])
  (:import (org.eclipse.aether.graph Dependency
                                     DependencyNode
                                     DefaultDependencyNode)
           (org.eclipse.aether.repository RemoteRepository
                                          RemoteRepository$Builder
                                          RepositoryPolicy)
           (org.eclipse.aether.collection CollectRequest)
           (org.eclipse.aether.deployment DeployRequest)
           (org.eclipse.aether.installation InstallRequest)
           (org.eclipse.aether.resolution ArtifactRequest
                                          DependencyRequest)))

^{:refer lucid.aether.request/dependency :added "1.2"}
(comment "creates a `Dependency` object from map"

  (object/from-data {:artifact '[im.chit/hara "2.4.8"]}
                    Dependency)
  ;;=> #dep{:artifact "im.chit:hara:jar:2.4.8",
  ;;        :exclusions [],
  ;;        :optional false,
  ;;        :scope "",
  ;;        :optional? false}
  )

^{:refer lucid.aether.request/dependency-node :added "1.2"}
(comment "creates a `DependencyNode` object from map"

  (object/from-data {:artifact '[im.chit/hara "2.4.8"]}
                    DependencyNode)
  ;;=> #dep.node {:children [],
  ;;              :relocations [],
  ;;              :repositories [],
  ;;              :managed-bits 0,
  ;;              :artifact "im.chit:hara:jar:2.4.8",
  ;;              :aliases [],
  ;;              :request-context "",
  ;;              :data {}}
  )

^{:refer lucid.aether.request/artifact-request :added "1.2"}
(comment "creates an `ArtifactRequest` object from map"

  (artifact-request
   {:artifact "im.chit:hara:2.4.8"
    :repositories [{:id "clojars"
                    :authentication {:username "zcaudate"
                                     :password "hello"}
                    :url "https://clojars.org/repo/"}]})
  ;;=> #req.artifact{:artifact "im.chit:hara:jar:2.4.8",
  ;;                 :repositories [{:id "clojars",
  ;;                                 :url "https://clojars.org/repo/"
  ;;                                 :authentication {:username "zcaudate", :password "hello"}}],
  ;;                 :request-context ""}
  )

^{:refer lucid.aether.request/collect-request :added "1.2"}
(comment "creates a `CollectRequest` object from map"

  (collect-request
   {:root {:artifact "im.chit:hara:2.4.8"}
    :repositories [{:id "clojars"
                    :url "https://clojars.org/repo/"}]})
  ;;=> #req.collect{:root {:artifact "im.chit:hara:jar:2.4.8",
  ;;                       :exclusions [],
  ;;                       :optional false,
  ;;                       :scope "",
  ;;                       :optional? false}
  ;;                :repositories [{:id "clojars",
  ;;                                :url "https://clojars.org/repo/"}]}
  )

^{:refer lucid.aether.request/dependency-request :added "1.2"}
(comment "creates a `DependencyRequest` object from map"

  (dependency-request
   {:root {:artifact "im.chit:hara:2.4.8"}
    :repositories [{:id "clojars"
                    :url "https://clojars.org/repo/"}]})
  ;;=> #req.dependency{:root {:artifact "im.chit:hara:jar:2.4.8",
  ;;                          :exclusions [],
  ;;                          :optional false,
  ;;                          :scope "",
  ;;                          :optional? false}
  ;;                   :repositories [{:id "clojars",
  ;;                                   :url "https://clojars.org/repo/"}]}
  )

^{:refer lucid.aether.request/deploy-request :added "1.2"}
(comment "creates a `DeployRequest` object from map"

  (deploy-request
   {:artifacts [{:group "im.chit"
                 :artifact "hara.string"
                 :version "2.4.8"
                 :extension "jar"
                 :file "hara-string.jar"}]
    :repository {:id "clojars"
                 :url "https://clojars.org/repo/"
                 :authentication {:username "zcaudate"
                                  :password "hello"}}})
  ;;=> #req.deploy{:artifacts ["im.chit:hara.string:jar:2.4.8"]
  ;;               :repository {:id "clojars",
  ;;                            :authentication {:username "zcaudate", :password "hello"}
  ;;                            :url "https://clojars.org/repo/"}}
  )

^{:refer lucid.aether.request/install-request :added "1.2"}
(comment "creates a `InstallRequest` object from map"

  (install-request
   {:artifacts [{:group "im.chit"
                 :artifact "hara.string"
                 :version "2.4.8"
                 :extension "jar"
                 :file "hara-string.jar"}
                {:group "im.chit"
                 :artifact "hara.string"
                 :version "2.4.8"
                 :extension "pom"
                 :file "hara-string.pom"}]})
  ;;=> #req.install{:artifacts ["im.chit:hara.string:jar:2.4.8"
  ;;                            "im.chit:hara.string:pom:2.4.8"]
  ;;                :metadata []}
  )
