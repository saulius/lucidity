(ns lucid.publish.link.api)

(defn link-apis
  [{:keys [references] :as interim} name]
  interim
  #_(update-in interim [:articles name :elements]
             (fn [elements]
               (mapv (fn [element]
                       (if (-> element :type (= :reference))
                         (let [{:keys [refer mode]} element
                               refer (symbol refer)
                               nsp (symbol (.getNamespace refer))
                               var (symbol (.getName refer))
                               mode (or mode :source)
                               code (get-in references [nsp var mode])
                               code (case mode
                                      :source code
                                      :docs   (process-doc-nodes code))]
                           (-> element
                               (assoc :type :reference
                                      :indentation (case mode :source 0 :test 2)
                                      :code code
                                      :mode mode)
                               (update-in [:title] #(or % (str (clojure.core/name mode) " of <i>" refer "</i>")))))
                         element))
                     elements))))
