(ns lucid.publish.render
  (:require [hara.io.project :as project]))

(defn render-html
  ([interim]
   (doseq [name (-> interim :articles keys)]
     (render-html interim name)))
  ([interim name]
   (render-html interim name (project/project)))
  ([interim name project]
   (let [elements (get-in interim [:articles name :elements])]
     )))
