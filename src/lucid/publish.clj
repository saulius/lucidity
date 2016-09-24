^{:name "hello-world"
  :template "home.html"
  :title "lucidity"
  :subtitle "tools for clarity"}
(ns lucid.publish
  (:require [hara.io.project :as project]
            [lucid.publish
             [collect :as collect]
             [link :as link]
             [parse :as parse]]
            [lucid.unit :as unit]
            [hara.io.file :as fs]
            [clojure.string :as string]))

(defn lookup-path [input project]
  (let [input (if (instance? clojure.lang.Namespace input)
                 (.getName input)
                 input)]
    (cond (string? input)
          (or (get-in project [:publish :files input :input])
              input)

          (symbol? input)    
          (unit/lookup-namespace input)
          
          :else (throw (Exception. (str "Cannot process input: " input))))))

(defn lookup-name [path project]
  (->> (-> project :publish :files)
       (filter (fn [[k {:keys [input]}]]
                 (let [tail (subs (str (fs/path path))
                                  (inc (count (:root project))))]
                   (= input tail))))
       (ffirst)))

(defn tagged-name [elements]
  )

(defn simple-name [path]
  (-> (fs/path path)
      (.getFileName)
      str
      (string/split #"\.")
      first))

(defn publish
  ([] (publish *ns*))
  ([x] (cond (keyword? x)
             (publish *ns* x)

             :else (publish x :html)))
  ([input type] (publish input type (project/project)))
  ([input type project]
   (let [path     (lookup-path input project)
         elements (parse/parse-file path project)
         name     (or (lookup-name path project)
                      (tagged-name elements)
                      (simple-name path))]
     (-> (assoc-in {} [:articles name :elements] elements)
         (collect/collect-global name)
         (collect/collect-article name)
         (collect/collect-namespaces name)
         (collect/collect-tags name)
         (collect/collect-citations name)
         (link/link-namespaces name)
         (link/link-references name)
         (link/link-numbers name)
         (link/link-tags name)
         (link/link-anchors-lu name)
         (link/link-anchors name)
         (link/link-stencil name)))))

(comment
  (parse/parse-file (lookup-path *ns*) (project/project))
  (publish :pdf)
  (publish)

  (def project (project/project))
  
  (-> (project/project)
      :site
      :files)

  (-> (project/project)
      :root))
  
