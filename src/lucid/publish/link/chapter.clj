(ns lucid.publish.link.chapter)

(defn link-chapters
  "" [interim name]
  (let [apis (->> (get-in interim [:articles name :elements])
                  (filter #(-> % :type (= :api)))
                  (map (juxt :namespace :table))
                  (into {}))]
    (update-in interim [:articles name :elements]
               (fn [elements]
                 (mapv (fn [{:keys [type link] :as element}]
                         (if (= type :chapter)
                           (assoc element :table (get apis link))
                           element))
                       elements)))))
