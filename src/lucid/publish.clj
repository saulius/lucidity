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
          (or (if-let [imap (get-in project [:publish :files input])]
                (:input imap))
              (if (fs/exists? input)
                input))

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
  (->> (filter #(-> % :type (= :ns-form)) elements)
       (map :meta)
       (apply merge)
       :name))

(defn simple-name [path]
  (-> (fs/path path)
      (.getFileName)
      str
      (string/split #"\.")
      first))

(defn publish-prepare
  ([elements name]
   (publish-prepare elements name {}))
  ([elements name out]
   (-> (assoc-in out [:articles name :elements] elements)
       (collect/collect-global name)
       (collect/collect-article name)
       (collect/collect-namespaces name)
       (collect/collect-tags name)
       (collect/collect-citations name)
       (link/link-namespaces name)
       ;;(link/link-references name)
       ;;(link/link-api name)
       (link/link-numbers name)
       (link/link-tags name)
       (link/link-anchors-lu name)
       (link/link-anchors name)
       (link/link-stencil name))))

(defn publish-interim
  [inputs type project]
  (reduce (fn [out input]
            (let [path        (lookup-path input project)
                  elements    (if path (parse/parse-file path project) [])
                  name        (if path
                                (or (lookup-name path project)
                                    (tagged-name elements)
                                    (simple-name path))
                                (str input))]
               (publish-prepare elements name out)))
           {}
           inputs))

(defn publish
  ([] (publish [*ns*]))
  ([x] (cond (keyword? x)
             (publish *ns* x)

             :else
             (publish x :html)))
  ([inputs type] (publish inputs type (project/project)))
  ([inputs type project]
   (let [inputs  (if (vector? inputs) inputs [inputs])
         interim (publish-interim inputs type project)]
     interim)))

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
  
