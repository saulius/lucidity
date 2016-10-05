(ns lucid.publish.collect.base
  (:require [hara.data.nested :as nested]))

(defn collect-namespaces
  ""
  [{:keys [articles] :as interim} name]
  (let [all    (->> (get-in articles [name :elements])
                    (filter #(-> % :type (= :ns-form))))
        meta   (-> all first :meta)
        namespaces (->> all
                        (map (juxt :ns identity))
                        (into {}))]
    (-> interim
        (update-in [:articles name :meta] (fnil nested/merge-nested {}) meta)
        (update-in [:namespaces] (fnil nested/merge-nested {}) namespaces)
        (update-in [:articles name :elements]
                   (fn [elements] (filter #(-> % :type (not= :ns-form)) elements))))))

(defn collect-article-metas
  ""
  [{:keys [articles] :as interim} name]
  (let [articles (->> (get-in articles [name :elements])
       (filter #(-> % :type (= :article)))
       (apply nested/merge-nested {}))]
    (-> interim
        (update-in [:articles name :meta] (fnil nested/merge-nested {}) (dissoc articles :type))
        (update-in [:articles name :elements]
                   (fn [elements] (filter #(-> % :type (not= :article)) elements))))))

(defn collect-global-metas
  ""
  [{:keys [articles] :as interim} name]
  (let [global (->> (get-in articles [name :elements])
                    (filter #(-> % :type (= :global)))
                    (apply nested/merge-nested {}))]
    (-> interim
        (update-in [:global] (fnil nested/merge-nested {}) (dissoc global :type))
        (update-in [:articles name :elements]
                   (fn [elements] (filter #(-> % :type (not= :global)) elements))))))

(defn collect-tags
  ""
  [{:keys [articles] :as interim} name]
  (->> (get-in articles [name :elements])
       (reduce (fn [m {:keys [tag] :as ele}]
                                (cond (nil? tag) m

                                      (get m tag) (do (println "There is already an existing tag for" ele)
                                                      m)
                                      :else (conj m tag)))
               #{})
       (assoc-in interim [:articles name :tags])))

(defn collect-citations
  ""
  [{:keys [articles] :as interim} name]
  (let [citations (->> (get-in articles [name :elements])
                       (filter #(-> % :type (= :citation))))]
    (-> interim
        (assoc-in  [:articles name :citations] citations)
        (update-in [:articles name :elements]
                   (fn [elements] (filter #(-> % :type (not= :citation)) elements))))))
