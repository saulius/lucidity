^{:title "publish"
  :subtitle "source code of lucid.publish"
  :author "Chris Zheng"
  :email "z@caudate.me"}
(ns lucid.publish
  (:require [hara.io.project :as project]
            [lucid.publish
             [prepare :as prepare]
             [render :as render]
             [theme :as theme]]
            [clojure.java.io :as io]
            [hara.io.file :as fs]))

(def ^:dynamic *output* "docs")

(defn output-path [project]
  (let [output-dir (or (-> project :publish :output)
                         *output*)]
    (fs/path (:root project) output-dir)))

(defn copy-assets
  ([]
   (theme/apply-settings copy-assets))
  ([settings project]
   (let [source (theme/template-path settings project)
         output (output-path project)]
     (doseq [entry (:copy settings)]
       (let [dir   (fs/path source entry)
             files (->> (fs/select dir)
                        (filter fs/file?))]
         (doseq [in files]
           (let [out (fs/path output (str (fs/relativize dir in)))]
             (fs/create-directory (fs/parent out))
             (fs/copy-single in out {:options [:replace-existing :copy-attributes]}))))))))

(defn apply-with-options [f & args]
  (let [[args opts]  [(butlast args) (last args)]
        project (project/project)
        theme   (or (:theme opts)
                    (-> project :publish :theme))
        settings (merge (theme/load-settings theme project)
                        opts)]
     (when (:refresh settings)
       (theme/deploy settings project)
       (copy-assets settings project))
     (apply f (concat args [settings project]))))

(defn publish
  ([] (publish [*ns*] {}))
  ([x] (cond (map? x)
             (publish [*ns*] x)

             :else
             (publish x {})))
  ([inputs opts]
   (apply-with-options publish inputs opts))
  ([inputs settings project]
   (let [inputs (if (vector? inputs) inputs [inputs])
         ns->symbol (fn [x] (if (instance? clojure.lang.Namespace x)
                              (.getName x)
                              x))
         inputs (map ns->symbol inputs)
         interim (prepare/prepare inputs project)
         names (-> interim :articles keys)
         out-dir (fs/path (-> project :root)
                          (or (-> project :publish :output) *output*))]
     (fs/create-directory out-dir)
     (println "KEYS:" inputs names out-dir)
     (doseq [name names]
       (spit (str (fs/path (str out-dir) (str name ".html")))
             (render/render interim name settings project))))))

(defn publish-all
  ([] (publish-all {}))
  ([opts]
   (apply-with-options publish-all opts))
  ([settings project]
   (let [template (theme/template-path settings project)
         output   (output-path project)
         files (-> project :publish :files keys vec)]
     (publish files settings project))))

(comment
  (def settings (theme/load-settings "martell"))
  (def settings)
  
  (theme/load-settings "stark")
  
  (output-path (project/project))
  (theme/deploy)
  (publish "index")
  (publish "index")
  (publish "lucid-mind" {:theme "stark" :refresh true})
  (publish "lucid-publish")
  (publish "lucid-query" {:theme "stark"})
  (publish "lucid-mind" {:theme "stark"})
  (publish "lucid-test")
  (publish "lucid-core")
  (publish "lucid-query" {:theme "stark" :refresh true})
  (publish {:theme "stark" :refresh true})
  (copy-assets)
  (publish-all {:theme "stark" :refresh true})
  (publish "lucid-mind")
  (publish "lucid-query")
  (publish "lucid-library")
  (publish "lucid-core")

  (publish-all)
  (template-deploy)
  (template-copy)

  (def interim (prepare/prepare ["index"]))
  (lucid.publish.theme.stark/render-top-level interim)
  
  (fs/option )
  (:atomic-move :create-new :skip-siblings :read :continue :create :terminate :copy-attributes :append :truncate-existing :sync :follow-links :delete-on-close :write :dsync :replace-existing :sparse :nofollow-links :skip-subtree)
  (copy-template "martell")
  (settings)
  
  (java.nio.file.Files/copy (.openStream (io/resource "clojure/core/match.clj"))
                            (fs/path "match.clj")
                            (make-array java.nio.file.CopyOption 0))
  (deploy-template "martell")
  (theme/load-settings "martell")
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
