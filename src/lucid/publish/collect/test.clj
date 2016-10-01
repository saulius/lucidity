(ns lucid.publish.collect.test
  (:require [hara.test :as test]
            [hara.data.nested :as nested]))

(defn collect-tests
  "collects all the tests and runs them"
  {:added "1.2"}
  [{:keys [articles] :as interim} name]
  (let [all    (->> (get-in articles [name :elements])
                    (filter #(-> % :type (= :test))))]
    interim))
