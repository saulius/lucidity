(ns lucid.publish.prepare
  (:require [hara.io
             [file :as fs]
             [project :as project]]
            [lucid.test :as unit]
            [lucid.publish
             [collect :as collect]
             [link :as link]
             [parse :as parse]]))

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
  ([key] (lookup-meta key (project/project)))
  ([key project]
   (cond (map? key) key

         (string? key)
         (if-let [m (get-in project [:publish :files key])]
           (assoc m :name key))
         
         (symbol? key)
         (if-let [path (unit/lookup-namespace key)]
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
         (collect/collect-global name)
         (collect/collect-article name)
         (collect/collect-namespaces name)
         (collect/collect-tags name)
         (collect/collect-citations name)
         (link/link-namespaces name)
         ;;(link/link-references name)
         ;;(link/link-api name)
         ;;(link/link-tests name)
         (link/link-numbers name)
         (link/link-tags name)
         (link/link-anchors-lu name)
         (link/link-anchors name)
         (link/link-stencil name)))))

(defn prepare
  "prepares an interim structure for many inputs"
  {:added "1.2"}
  ([inputs] (prepare inputs (project/project)))
  ([inputs project]
   (reduce (fn [out input]
             (-> input
                 (lookup-meta project)
                 (prepare-single project out)))
           {}
           inputs)))
