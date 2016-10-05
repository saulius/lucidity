(ns lucid.publish.link.namespace)

(defn link-namespaces
  ""
  [{:keys [namespaces articles] :as interim} name]
  (update-in interim [:articles name :elements]
             (fn [elements]
               (mapv (fn [element]
                       (if (= :ns (:type element))
                         (let [{:keys [ns code]} element]
                           (assoc element
                                  :type :ns
                                  :indentation 0
                                  :code (get-in namespaces [ns :code])))
                         element))
                     elements))))
