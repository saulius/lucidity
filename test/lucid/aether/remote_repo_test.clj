(ns lucid.aether.remote-repo-test
  (:use hara.test)
  (:require [hara.object :as object])
  (:import (org.eclipse.aether.repository RemoteRepository
                                          RemoteRepository$Builder
                                          RepositoryPolicy)))

^{:refer lucid.aether.remote-repo/repository-policy :added "1.2"}
(comment "creates a `RepositoryPolicy` from map"
  
  (object/from-data {:checksum-policy "warn",
                     :update-policy "daily",
                     :enabled? true}
                    RepositoryPolicy)
  ;;=> #policy{:checksum-policy "warn", :update-policy "daily", :enabled? true}
  )

^{:refer lucid.aether.remote-repo/remote-repository-builder :added "1.2"}
(comment "creates a `RemoteRepository$Builder` from map"
  
  (object/from-data {:id "clojars"
                     :type "default"
                     :url "http://clojars.org/repo/"}
                    RemoteRepository$Builder)
  ;;=> #builder.remote{:repository-manager false
  ;;                   :type "default"
  ;;                   :default-policy   {:checksum-policy "warn",
  ;;                                      :update-policy "daily",
  ;;                                      :enabled? true},
  ;;                   :release-policy   {:checksum-policy "warn"
  ;;                                      :update-policy "daily"
  ;;                                      :enabled? true}
  ;;                   :snapshots-policy {:checksum-policy "warn",
  ;;                                      :update-policy "daily",
  ;;                                      :enabled? true},
  ;;                   :id "clojars"
  ;;                   :url "http://clojars.org/repo/"}
  )

^{:refer lucid.aether.remote-repo/remote-repository :added "1.2"}
(comment "creates a `RemoteRepository` from map"

  (object/from-data {:id "clojars"
                     :type "default"
                     :url "http://clojars.org/repo/"
                     :authentication {:username "zcaudate"
                                      :password "hello"}}
                    RemoteRepository)
  ;;=> #remote{:authentication {:username "zcaudate", :password "hello"},
  ;;           :content-type "default",
  ;;           :host "clojars.org",
  ;;           :id "clojars",
  ;;           :mirrored-repositories [],
  ;;           :protocol "http",
  ;;           :url "http://clojars.org/repo/",
  ;;           :repository-manager? false}
  )
