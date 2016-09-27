^{:name "hello-world"
  :template "home.html"
  :title "lucidity"
  :subtitle "tools for clarity"}
(ns lucid.publish
  (:require [hara.io.project :as project]
            [lucid.publish
             [prepare :as prepare]
             [render :as render]
             [template :as template]]
            [clojure.java.io :as io]
            [hara.io.file :as fs]))

(def ^:dynamic *output* "docs")
(def ^:dynamic *template* "template")

(defn publish
  ([] (publish [*ns*]))
  ([inputs]
   (let [project (project/project)
         theme  (-> project :publish :template :theme)
         settings (template/load-settings theme project)]
     (publish inputs settings project)))
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
     (doseq [name names]
       (spit (str (fs/path (str out-dir) (str name ".html")))
             (render/render interim name settings project))))))

(defn template-path [project]
  (let [template-dir (or (-> project :publish :template :path)
                         *template*)]
    (fs/path (:root project) template-dir)))

(defn output-path [project]
  (let [output-dir (or (-> project :publish :output)
                         *output*)]
    (fs/path (:root project) output-dir)))

(defn template-deploy
  ([]
   (let [project (project/project)
         theme   (-> project :publish :template :theme)
         settings (template/load-settings theme project)]
     (template-deploy settings project)))
  ([settings project]
   (let [target (template-path project)
         _  (if (fs/exists? target)
              (throw (Exception. (format "Template already deployed in %s"
                                         target))))
         inputs   (mapv (juxt (fn [path]
                                (-> (str (:resource settings) "/" path)
                                    (io/resource)
                                    (.openStream)))
                              identity)
                        (:manifest settings))]
     (doseq [[stream filename] inputs]
       (let [out (fs/path target filename)]
         (fs/create-directory (fs/parent out))
         (fs/write stream out))))))

(defn template-copy
  ([]
   (let [project (project/project)
         theme   (-> project :publish :template :theme)
         settings (template/load-settings theme project)]
     (template-copy settings project)))
  ([settings project]
   (let [source (template-path project)
         output (output-path project)
         files  (->> (:copy settings)
                     (mapcat (fn [dir]
                               (let [partial (fs/path source dir)
                                     files (->> (fs/select partial)
                                                (filter fs/file?))]
                                 (map (juxt identity
                                            (fn [f]
                                              (fs/path output (str (fs/relativize partial f))))) files)))))]
     (doseq [[in out] files]
       (fs/create-directory (fs/parent out))
       (fs/copy-single in out {:options [:replace-existing :copy-attributes]})))))

(defn publish-all
  ([]
   (let [project (project/project)
         theme  (-> project :publish :template :theme)
         settings (template/load-settings theme project)]
     (publish-all settings project)))
  ([settings project]
   (let [template (template-path project)
         output   (output-path project)
         _  (if-not   (fs/exists? template)
              (template-deploy settings project))
         files (-> project :publish :files keys vec)]
     (template-copy settings project)
     (publish files settings project))))

(comment
  (def settings (template/load-settings "martell"))
  
(output-path (project/project))
  
  (publish "index")
  (publish "index") 

  (publish "lucid-mind")
  (publish "lucid-query")
  (publish "lucid-library")
  (publish "lucid-core")

  (publish-all)
  (template-deploy)
  (template-copy)
  
  (fs/option )
  (:atomic-move :create-new :skip-siblings :read :continue :create :terminate :copy-attributes :append :truncate-existing :sync :follow-links :delete-on-close :write :dsync :replace-existing :sparse :nofollow-links :skip-subtree)
  (copy-template "martell")
  (settings)
  
  (java.nio.file.Files/copy (.openStream (io/resource "clojure/core/match.clj"))
                            (fs/path "match.clj")
                            (make-array java.nio.file.CopyOption 0))
  (deploy-template "martell")
  (template/load-settings "martell")
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
  
