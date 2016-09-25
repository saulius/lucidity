(ns lucid.publish.render
  (:require [hara.io.project :as project]))

(defmulti render (fn [type interim project] type))

(defmethod render :html
  [type interim project]
  (doseq [name (-> interim :articles keys)]
    (render-html interim name project)))
