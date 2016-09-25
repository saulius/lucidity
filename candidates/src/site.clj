(ns lucid.publish)

(defn select-values
  "filters values of a map that fits the predicate
   (filter-pred string? {:a \"valid\" :b 0})
   => {:a \"valid\"}"
  {:added "0.1"}
  [m pred]
  (reduce-kv (fn [m k v] (if (or (= pred v)
                                 (pred v))
                           (assoc m k v)
                           m))
             {} m))

(defn generate
  "generates the tree outline for rendering"
  {:added "0.1"}
  [{:keys [project] :as interim} name]
  (let [meta       (-> project :documentation :files (get name))
        interim      (prepare-article interim name (:input meta))
        elements   (get-in interim [:articles name :elements])
        structure  (structure/structure elements)]
    structure))

(defn find-placeholders
  "finds elements with `@=` tags

   (find-includes \"<@=hello> <@=world>\")
   => #{:hello :world}"
  {:added "0.1"}
  [html]
  (->> html
       (re-seq #"<@=([^>^<]+)>")
       (map second)
       (map keyword)
       set))

(defn prepare-placeholders
  "prepare template accept placeholders"
  {:added "0.1"}
  [name placeholders interim]
  (let [no-doc (->> (filter (fn [[k v]] (#{:article :navigation} v)) placeholders)
                    empty?)]
    (cond no-doc
          placeholders

          :else
          (let [elements (generate interim name)]
            (reduce-kv (fn [out k v]
                         (assoc out k (case v
                                        :article    (render/render-article elements interim)
                                        :navigation (render/render-navigation elements interim)
                                        v)))
                       {}
                       placeholders)))))

(defn render-entry
  "helper function that is called by both render-single and render-all"
  {:added "0.1"}
  [name entry]
  (println "Rendering" name "....")
  (try
    (let [project        (project/project)
          opts           (:site project)
          entry          (merge (select-values project string?)
                                (-> opts :template :defaults)
                                entry)
          template-path  (str (:root project) "/" (-> opts :template :path) "/" (:template entry))
          output-path    (str (:root project) "/" (:output opts) "/"(str name ".html"))
          template       (slurp template-path)
          includes       (->> (find-includes template)
                              (select-keys entry))
          includes       (prepare-includes name includes interim)
          html           (render/replace-template template includes opts project)]
      (spit output-path html)
      (println "SUCCESS"))
    (catch Throwable t
      (println "ERROR")
      (.printStackTrace t)
      (println "Unable to render" name))))
