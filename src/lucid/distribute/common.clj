(ns lucid.distribute.common)

(defrecord FileInfo []
  Object
  (toString [this] (-> this :path)))

(defmethod print-method FileInfo [v w]
  (.write w (str v)))