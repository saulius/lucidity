(ns lucid.publish.collect.api
  (:require [lucid.core.code :as code]
            [lucid.publish.collect.reference :as reference]))

(defn collect-apis
  ""
  {:added "1.2"}
  [{:keys [articles project] :as interim} name]
  (let [all    (->> (get-in articles [name :elements])
                    (filter #(-> % :type (= :api))))
        namespaces (-> (map (comp symbol :namespace) all))]
    (-> interim
        (update-in [:references]
                   (fnil (fn [references]
                           (reference/reference-namespaces references
                                                           (:lookup project)
                                                           namespaces))
                         {})))))

(comment
  "DO NOT DELETE!!!!!"
  
  (:lookup PROJECT)
  (-> (parse/parse-file
       "test/documentation/hara_zip.clj" PROJECT)
      (->> (assoc-in {} [:articles "hara-zip" :elements]))
      (assoc :project PROJECT)
      (collect-references "hara-zip")
      :references
      keys)
  (hara.zip hara.zip.base))

(comment
   (do (require '[lucid.publish :as publish]
                '[lucid.publish.theme]
                '[hara.io.project :as project]
                '[lucid.publish.parse :as parse])
       
       (def project-file "/Users/chris/Development/chit/hara/project.clj")
       
       (def PROJECT (let [project (project/project project-file)] 
                      (assoc project :lookup (project/file-lookup project))))))
