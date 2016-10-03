(ns lucid.publish.prepare
  (:require [hara.io
             [file :as fs]
             [project :as project]]
            [lucid.publish [parse :as parse]]
            [lucid.publish.collect
             [api :refer  [collect-apis]]
             [base :refer [collect-article-metas
                           collect-citations
                           collect-global-metas
                           collect-namespaces
                           collect-tags]]
             [reference :refer [collect-references]]]
            [lucid.publish.link
             [anchor :refer [link-anchors
                             link-anchors-lu]]
             [api :refer [link-apis]]
             [chapter :refer [link-chapters]]
             [namespace :refer [link-namespaces]]
             [number :refer [link-numbers]]
             [reference :refer [link-references]]
             [stencil :refer [link-stencil]]
             [tag :refer [link-tags]]
             [test :refer [link-tests]]]))

(defn lookup-meta
  "takes a key and looks up the associated meta information
   (lookup-meta \"index\")
   => {:template \"home.html\",
       :title \"lucidity\",
       :subtitle \"tools for clarity\",
       :name \"index\"}
 
   (lookup-meta 'documentation.lucid-mind)
   => {:input \"test/documentation/lucid_mind.clj\",
       :title \"mind\",
       :subtitle \"simple, contemplative reflection\",
       :name \"lucid-mind\"}"
  {:added "1.2"}
  ([key lookup project]
   (cond (map? key) key

         (string? key)
         (if-let [m (get-in project [:publish :files key])]
           (assoc m :name key))
         
         (symbol? key)
         (if-let [path (get lookup key)]
           (let [tail (str (fs/relativize (:root project) path))
                 [k m] (->> (-> project :publish :files)
                            (filter (fn [[k {:keys [input]}]]
                                      (= input tail)))
                            first)]
             (if m
               (assoc m :name k)
               {:name (str key) :input tail})))         
         
         :else (throw (Exception. (format "Cannot process key: %s, type %s"
                                          key
                                          (.getName (type key))))))))

(defn prepare-single
  "processes a single meta to generate an interim structure
 
   (prepare-single (lookup-meta \"index\"))
   => (contains-in {:articles {\"index\" map?},
                    :global map?
                    :namespaces map?
                    :anchors-lu {\"index\" map?},
                    :anchors {\"index\" map?}})"
  {:added "1.2"}
  ([opts] (prepare-single opts (project/project) {}))
  ([{:keys [name input] :as meta} project out]
   (let [elements (if input (parse/parse-file input project) [])]
     (-> out
         (assoc :project project)
         (assoc-in [:articles name :elements] elements)
         (assoc-in [:articles name :meta] meta)
         (collect-global-metas name)
         (collect-article-metas name)
         (collect-namespaces name)
         (collect-references name)
         (collect-apis name)
         (collect-tags name)
         (collect-citations name)
         (link-namespaces name)
         (link-references name)
         (link-apis name)
         (link-chapters name)
         (link-tests name)
         (link-numbers name)
         (link-tags name)
         (link-anchors-lu name)
         (link-anchors name)
         (link-stencil name)))))

(defn prepare
  "prepares an interim structure for many inputs"
  {:added "1.2"}
  ([inputs]
   (let [project (project/project)]
     (assoc project :lookup (project/file-lookup project)))
   (prepare inputs (project/project)))
  ([inputs {:keys [lookup] :as project}]
   (reduce (fn [out input]
             (-> input
                 (lookup-meta lookup project)
                 (prepare-single project out)))
           {}
           inputs)))
