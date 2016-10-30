(ns lucid.distribute.common
  (:require [hara.io.file :as fs]))

(defrecord FileInfo []
  Object
  (toString [this] (-> this :path)))

(defmethod print-method FileInfo [v w]
  (.write w (str v)))
  
(defn interim-path
  "shows the interim path where the files will be split

   (interim-path (project/project))
   ;;=> \"/Users/chris/Development/chit/lucidity/target/interim\"
   "
  {:added "1.2"}
  [project]
  (str (:root project) "/target/interim"))