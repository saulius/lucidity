(ns lucid.distribute.analyser.base
  (:require [hara.io.file :as fs]))

(defmulti file-info
  "returns the file-info
 
   (file-info \"src/lucid/distribute/analyser.clj\")
   => '{:exports #{[:clj lucid.distribute.analyser]},
        :imports #{[:clj lucid.distribute.analyser.java]
                   [:clj lucid.distribute.analyser.cljs]
                   [:clj lucid.distribute.analyser.base]
                   [:clj clojure.string]
                  [:clj lucid.distribute.analyser.clj]}}"
  {:added "1.2"}
  fs/file-type)

(defmethod file-info :default
  [file]
  {:file file
   :exports #{}
   :imports #{}})
