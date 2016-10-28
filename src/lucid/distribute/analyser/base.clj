(ns lucid.distribute.analyser.base
  (:require [clojure.string :as string]))

(defn file-type
  "encodes the type of file as a keyword
 
   (file-type \"hello.clj\")
   => :clj
 
   (file-type \"hello.java\")
   => :java"
  {:added "1.2"}
  [file]
  (-> (str file)
      (string/split #"\.")
      last
      keyword))

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
  file-type)

(defmethod file-info :default
  [file]
  {:file file
   :exports #{}
   :imports #{}})
