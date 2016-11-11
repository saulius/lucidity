(ns lucid.aether.local-repo-test
  (:use hara.test)
  (:require [lucid.aether.local-repo :refer :all]
            [hara.object :as object])
  (:import [org.eclipse.aether.repository LocalRepository]))
  
^{:refer lucid.aether.local-repo/local-repo :added "1.2"}
(fact "creates a `LocalRepository` from a string:"

  (local-repo)
  => LocalRepository ;; #local "<.m2/repository>"

  ;; hooks into hara.object
  (-> (local-repo "/tmp")
      (object/to-data))
  => "/tmp")
