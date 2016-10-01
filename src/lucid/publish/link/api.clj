(ns lucid.publish.link.api
  (:require [hara.io.file :as fs]
            [lucid.core.code :as code]))

(defn external-vars [lookup ns]
  (if-let [path (lookup ns)]
    (->> (fs/code path)
         (filter #(-> % first (= 'ns/import)))
         (mapcat #(->> % rest (partition 2)))
         (map vec)
         (into {}))))

(defn create-api-table [references project namespace]
  (let [lookup  (:lookup project)
        all-vars (-> (external-vars lookup namespace)
                     (assoc namespace :all))
        live-vars (do (require namespace)
                      (ns-interns namespace))]
    (reduce-kv (fn [table ns vals]
                 (let [relative-to-root #(->> % (fs/relativize (:root project)) str)
                       vals (if (= :all vals)
                              (-> ns references keys)
                              vals)]
                   (reduce (fn [out v]
                             (let [entry (-> (get-in references [ns v])
                                             (update-in [:test :code] code/join-nodes)
                                             (update-in [:test :path] relative-to-root)
                                             (update-in [:source :path] relative-to-root)
                                             (assoc :origin (symbol (str ns "/" v))
                                                    :arglists (-> (get live-vars v)
                                                                  meta
                                                                  :arglists)))]
                               (assoc out v entry)))
                           table
                           vals)))
               {}
               all-vars)))

(defn link-apis
  [{:keys [references project] :as interim} name]
  (update-in interim [:articles name :elements]
             (fn [elements]
               (mapv (fn [{:keys [type namespace] :as element}]
                       (if (= type :api)
                         (-> element
                             (assoc :project project)
                             (assoc :table
                                    (create-api-table references
                                                      project
                                                      (symbol namespace))))
                         element))
                     elements))))

(comment
  "DO NOT DELETE!!!!!"
  (-> INTERIM
      (link-apis "hara-zip")
      (get-in [:articles "hara-zip" :elements])
      (->> (filter #(-> % :type (= :api))))
      (first)
      :table
      (keys))

  (keys (:references INTERIM)))

(comment
   (do (require '[lucid.publish :as publish]
                '[lucid.publish.theme :as theme]
                '[hara.io.project :as project]
                '[lucid.publish.parse :as parse]
                '[lucid.publish.collect.reference :refer [collect-references]]
                '[lucid.publish.collect.api :refer [collect-apis]])
       
       (def project-file "/Users/chris/Development/chit/hara/project.clj")
       
       (def PROJECT (let [project (project/project project-file)] 
                      (assoc project :lookup (project/file-lookup project))))

       (theme/load-settings "stark" PROJECT)

       (def INTERIM (-> (parse/parse-file
                         "test/documentation/hara_zip.clj" PROJECT)
                        (->> (assoc-in {} [:articles "hara-zip" :elements]))
                        (assoc :project PROJECT)
                        (collect-references "hara-zip"))))

   )
