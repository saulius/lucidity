(ns lucid.aether.local-repo
  (:require [clojure.java.io :as io]
            [hara.object :as object])
  (:import [org.eclipse.aether.repository LocalRepository]))

(def +default-local-repo+
  (-> (System/getProperty "user.home")
      (io/file ".m2" "repository")
      (.getAbsolutePath)))

(defn local-repo
  ""
  ([]
   (local-repo +default-local-repo+))
  ([path]
   (LocalRepository. path)))

(object/string-like

 LocalRepository
 {:tag "local"
  :read (fn [repo] (str (.getBasedir repo)))
  :write local-repo})

(local-repo)
