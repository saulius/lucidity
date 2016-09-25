(ns lucid.publish.render
  (:require [hara.io
             [project :as project]
             [file :as fs]]
            [hara.string
             [prose :as prose]]
            [clojure.java.io :as io]))

(defonce *theme* "martell")

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

(defn find-includes
  "finds elements with `@=` tags

   (find-includes \"<@=hello> <@=world>\")
   => #{:hello :world}"
  {:added "0.1"}
  [template]
  (->> template
       (re-seq #"<@=([^>^<]+)>")
       (map second)
       (map keyword)
       set))

(defn prepare-template
  ([interim name]
   (prepare-template interim name (load-settings) (project/project)))
  ([interim name settings project]
   (let [defaults (:defaults settings)
         opts (merge defaults
                     (get-in interim [:articles name :meta]))
         template (load-file (:template opts) settings project)
         includes (find-includes template)]
     [template (select-keys opts includes)])))

(defn render
  ([interim name]
   (render interim name (project/project)))
  ([interim name project]
   (let [settings (load-settings (-> project :publish :template :theme) project)
         [template includes] (prepare-template interim name settings project)]
     (reduce-kv (fn [^String html k v]
                  (let [value (cond (string? v) v

                                    (vector? v)
                                    (case (first v)
                                      :file (load-file (second v) settings project)
                                      :fn   ((second v) interim name)))]
                    (.replaceAll html
                                 (str "<@=" (clojure.core/name k) ">")
                                 (prose/escape-dollars value))))
                template
                includes))))

(comment
  (render
   (lucid.publish.prepare/prepare ["index"])
   "index")
  
  (prepare-template
   (lucid.publish.prepare/prepare ["index"])
   "index")
  
  (:defaults (load-settings))
  
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
   :fn #{:article :navigation}})
