(ns lucid.publish.collect.api
  (:require [lucid.core.code :as code]))

(defn collect-apis
  ""
  {:added "1.2"}
  [{:keys [articles] :as interim} name]
  (let [all    (->> (get-in articles [name :elements])
                    (filter #(-> % :type (= :api))))]
    interim))