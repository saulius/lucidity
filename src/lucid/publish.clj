^{:name "hello-world"
  :template "home.html"
  :title "lucidity"
  :subtitle "tools for clarity"}
(ns lucid.publish
  (:require [hara.io.project :as project]
            [lucid.publish
             [prepare :as prepare]
             [render :as render]]))

(defn publish
  ([] (publish [*ns*]))
  ([x] (cond (keyword? x)
             (publish *ns* x)

             :else
             (publish x :html)))
  ([inputs type] (publish inputs type (project/project)))
  ([inputs type project]
   (let [inputs (if (vector? inputs) inputs [inputs])
         ns->symbol (fn [x] (if (instance? clojure.lang.Namespace x)
                              (.getName x)
                              x))
         inputs (map ns->symbol inputs)
         interim (prepare/prepare inputs project)]
     (render type interim project))))

(comment
  (tagged-name (parse/parse-file (lookup-path *ns* (project/project)) (project/project)))
  
  (publish :pdf)
  (publish "index" :html)
  (def interim (publish))

  (keys interim)
  (:articles :meta :namespaces :anchors-lu :anchors)
  
  (:namespaces interim)

  (:anchors interim)

  (keys (get-in interim [:articles "hello-world"]))
  
  (:anchors-lu interim)
  
  (def project (project/project))
  
  (-> (project/project)
      :site
      :files)

  (-> (project/project)
      :root))
  
