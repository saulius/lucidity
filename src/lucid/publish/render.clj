(ns lucid.publish.render
  (:require [hara.io
             [project :as project]
             [file :as fs]]
            [hara.string
             [prose :as prose]]
            [clojure.java.io :as io]
            [lucid.publish.render
             [structure :as structure]]
            [lucid.publish
             [prepare :as prepare]
             [theme :as theme]]))

(defn find-includes
  ""
  [template]
  (->> template
       (re-seq #"<@=([^>^<]+)>")
       (map second)
       (map keyword)
       set))

(defn theme-file
  ""
  ([file]
   (load-file file (theme/load-settings)))
  ([file settings]
   (load-file file settings (project/project)))
  ([file settings project]
   (let [dir (or (:path settings) theme/*path*)]
     (slurp (str (fs/path (:root project) dir (:theme settings) file))))))

(defn render-init
  ""
  ([interim name]
   (let [project (project/project)
         theme  (-> project :publish :template :theme)
         settings (theme/load-settings theme project)]
     (render-init interim name settings project)))
  ([interim name settings project]
   (if-not (theme/deployed? settings project)
     (theme/deploy settings project))
   (let [opts (merge (select-keys project [:version :name :root :url :description])
                     (:defaults settings)
                     (-> project :publish :template)
                     (dissoc settings :manifest :defaults)
                     (get-in interim [:articles name :meta]))
         template (theme-file (:template opts) settings project)
         includes (find-includes template)]
     [template (select-keys opts includes)])))

(defn render
  ""
  ([interim name]
   (let [project (project/project)
         theme  (-> project :publish :template :theme)
         settings (theme/load-settings theme project)]
     (render interim name settings project)))
  ([interim name settings project]
   (let [[template includes] (render-init interim name settings project)
         interim (if (:structure settings)
                   (update-in interim
                              [:articles name :elements]
                              structure/structure)
                   interim)]
     (reduce-kv (fn [^String html k v]
                  (let [value (cond (string? v) v

                                    (vector? v)
                                    (case (first v)
                                      :file  (theme-file (second v) settings project)
                                      :fn    ((second v) interim name)))]
                    (.replaceAll html
                                 (str "<@=" (clojure.core/name k) ">")
                                 (prose/escape-dollars value))))
                template
                includes))))

(comment
  (theme/deployed?)
  
  (prepare-template
   (lucid.publish.prepare/prepare ["index"])
   "index")
  
  (render
   (lucid.publish.prepare/prepare ["index"])
   "index"))
