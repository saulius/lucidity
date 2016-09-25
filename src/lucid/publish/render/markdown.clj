(ns lucid.publish.render.markdown
  (:require [hara.io.project :as project]))
  
(defn render-markdown 
  ([interim]
   (doseq [name (-> interim :articles keys)]
     (render-markdown interim name)))
  ([interim name]
   (render-markdown interim name (project/project)))
  ([interim name project]
   (let [elements (get-in interim [:articles name :elements])])))