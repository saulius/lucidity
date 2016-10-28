(ns lucid.distribute.analyser.base
  (:require [clojure.string :as string]))

(defn file-type [file]
  (-> (str file)
      (string/split #"\.")
      last
      keyword))

(defmulti file-info file-type)

(defmethod file-info :default
  [file]
  {:file file
   :exports #{}
   :imports #{}})
