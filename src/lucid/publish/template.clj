(ns lucid.publish.template
  (:require [hara.io
             [project :as project]
             [file :as fs]]
            [clojure.java.io :as io])
  (:refer-clojure :exclude [load-file]))

(defonce ^:dynamic *theme* "martell")

(defn load-var [ns var]
  (-> (symbol (str ns "/" var))
      resolve
      deref))

(defn load-settings
  ([] (load-settings nil))
  ([theme] (load-settings theme (project/project)))
  ([theme project]
   (let [opts  (-> project :publish :template)
         theme (or theme (:theme opts) *theme*)
         ns (cond (string? theme)
                  (symbol (str "lucid.theme." theme))

                  (symbol? theme) theme

                  :else (throw (Exception. (format "Cannot load theme: %s" theme))))
         settings (do (require ns)
                      (load-var ns "settings"))]
     (->> (:render settings)
          (reduce-kv (fn [out k v]
                       (assoc-in out [:defaults k] [:fn (load-var ns v)]))
                     settings)
          (merge opts)))))

(defn load-file
  ([file]
   (load-file file (load-settings)))
  ([file settings]
   (load-file file settings (project/project)))
  ([file settings project]
   (if-let [dir (:path settings)]
     (slurp (str (fs/path (:root project) dir file)))
     (slurp (io/resource (str (:resource settings) "/" file))))))

(comment
  
  (:version (load-settings))
  
  {:theme "martell",
   :path "template",
   :resource "html/martell",
   :copy ["assets"],
   :render {:article "render-article", :navigation "render-navigation"},
   :defaults {:template "article.html",
              :navbar [:file "partials/navbar.html"]
              :sidebar [:file "partials/sidebar.html"]
              :footer [:file "partials/footer.html"]
              :dependencies [:file "partials/deps-web.html"]}
   :fn #{:article :navigation}}
  )
