(ns lucid.publish.collect.reference
  (:require [lucid.core.code :as code]
            [hara.io.file :as fs]
            [hara.data.nested :as nested]))

(defn find-import-namespaces [lookup ns]
  (if-let [path (lookup ns)]
    (->> (fs/code path)
         (filter #(-> % first (= 'ns/import)))
         (mapcat #(->> % rest (take-nth 2))))))

(defn reference-namespaces [references lookup namespaces]
  (let [missing   (remove references namespaces)
        imported  (->> missing
                       (mapcat #(find-import-namespaces lookup %))
                       (remove references))
        sources   (concat missing imported)
        tests     (map #(symbol (str % "-test")) sources)]
    (prn "STUFF:" sources tests)
    (reduce (fn [references ns]
              (if-let [file (lookup ns)]
                (->> (code/analyse-file file)
                     (nested/merge-nested references))
                references))
            references
            (concat sources tests))))

(defn collect-references
  ""
  {:added "1.2"}
  [{:keys [articles project] :as interim} name]
  (let [lookup (:lookup project)
        all    (->> (get-in articles [name :elements])
                    (filter #(-> % :type (= :reference))))
        namespaces (-> (map (comp symbol namespace symbol :refer) all))]
    (update-in interim [:references]
               (fnil (fn [references]
                       (reference-namespaces references lookup namespaces))
                     {}))))

(comment
  (:lookup PROJECT)
  (-> (parse/parse-file
       "test/documentation/hara_zip.clj" PROJECT)
      (->> (assoc-in {} [:articles "hara-zip" :elements]))
      (assoc :project PROJECT)
      (collect-references "hara-zip")
      :references
      keys)
  (hara.zip hara.zip.base)
  
  
  )




(comment
   (do (require '[lucid.publish :as publish]
                '[lucid.publish.theme]
                '[hara.io.project :as project]
                '[lucid.publish.parse :as parse])
       
       (def project-file "/Users/chris/Development/chit/hara/project.clj")
       
       (def PROJECT (let [project (project/project project-file)] 
                      (assoc project :lookup (project/file-lookup project)))))
   )
