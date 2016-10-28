(ns lucid.distribute.analyser
  (:require [clojure.string :as string]
            [lucid.distribute.analyser [java clj cljs]]))

(defn file-type [file]
  (-> (str file)
      (clojure.string/split #"\.")
      last
      keyword))
