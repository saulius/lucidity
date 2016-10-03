(ns lucid.publish.link.test
  (:require [hara.test.runner :as runner]
            [hara.io.file :as fs]))

(def ^:dynamic *run-tests* nil)

(defn failed-tests [facts]
    (->> facts
         (keep (fn [{:keys [results meta] :as fact}]
                 (let [failures  (->> results
                                      (filter #(and (-> % :from (= :verify))
                                                    (-> % :data false?)))
                                    (map (fn [{:keys [actual checker meta]}]
                                           (-> actual
                                               (select-keys [:data :form])
                                               (assoc :check (:form checker)
                                                      :code (select-keys meta [:line :column]))))))]
                   (if-not (empty? failures)
                     (assoc meta :output failures)))))))

(defn link-tests
  "collects all the tests and runs them"
  {:added "1.2"}
  [{:keys [project] :as interim} name]
  (if *run-tests*
    (let [path   (or (str (:root project) "/" (get-in project [:publish :files name :input]))
                     ((:lookup project) (symbol name)))
          rel    (str (fs/relativize (:root project) path))
          fails  (->> (fn [id sink] (load-file path))
                      runner/accumulate
                      failed-tests
                      (map (juxt :line identity))
                      (into {}))]
      (update-in interim [:articles name :elements]
                 (fn [elements]
                   (map (fn [{:keys [type] :as elem}]
                          (cond (not= :test type)
                                elem

                                :else
                                (if-let [failed (get fails (-> elem :line :row))]
                                  (assoc elem :failed failed :path rel)
                                  elem)))
                        elements))))
    interim))

(comment
  "DO NOT DELETE!!!!!"
  
  (:lookup PROJECT)
  (-> (parse/parse-file
       "test/documentation/hara_zip.clj" PROJECT)
      (->> (assoc-in {} [:articles "hara-zip" :elements]))
      (assoc :project PROJECT)
      (link-tests "hara-zip")
      (get-in [:articles "hara-zip" :elements]))

  (->> (fn [id sink]
         (load-file "/Users/chris/Development/chit/hara/test/documentation/hara_zip.clj"))
       runner/accumulate
       failed-tests)
  (map (juxt :line identity))
  (into {}))


(comment
   (do (require '[lucid.publish :as publish]
                '[lucid.publish.theme :as theme]
                '[hara.io.project :as project]
                '[lucid.publish.parse :as parse])
       
       (def project-file "/Users/chris/Development/chit/hara/project.clj")
       
       (def PROJECT (let [project (project/project project-file)] 
                      (assoc project :lookup (project/file-lookup project)))))


   )

