(ns lucid.publish.engine.plugin.api
  (:require [lucid.publish.render.util :as util]
            [clojure.string :as string]))

(defn entry-tag
  ""
  [ns var]
  (-> (munge (str "entry__" ns "__" var))
      (.replaceAll  "\\." "_")
      (.replaceAll "\\$" "STAR")))

(defn lower-first
  ""
  [s]
  (if (empty? s)
    ""
    (str (-> s (.charAt 0) str (.toLowerCase))
         (subs s 1))))

(defn api-entry-example
  ""
  [entry project]
  (let [code (-> (-> entry :test :code)
                 (or "")
                 (util/adjust-indent 2)
                 (string/trim))]
    (if (-> entry :test :path)
      [:pre
       [:h6 [:i [:a {:href (format "%s/blob/master/%s#L%d-L%d"
                                   (:url project)
                                   (-> entry :test :path)
                                   (-> entry :test :line :row)
                                   (-> entry :test :line :end-row))
                     :target "_blank"}
                 "link"]]]
       [:code {:class "clojure"} code]]
      [:pre {:class "error"}
       [:h6 "example not found"]
       [:code ""]])))

(defn api-entry-source
  "" [entry project var namespace]
  (if (nil? entry)
     [:pre {:class "error"} [:h6 "source not found"] [:code ""]]
     [:div {:class "entry-option"}
      [:h6
       (if-let [version (-> entry :meta :added)]
         [:a {:href (format "%s/blob/master/%s#L%d-L%d"
                            (:url project)
                            (-> entry :source :path)
                            (-> entry :source :line :row)
                            (-> entry :source :line :end-row))
                       :target "_blank"}
                   "v&nbsp;" version]
           [:i {:class "error version"} "NONE"])]
      [:div
       [:input {:class "source-toggle"
                :type "checkbox"
                :id (entry-tag (str "pre-" namespace)  var)
                }]
       [:label {:class "source-toggle"
                :for (entry-tag (str "pre-" namespace) var)} ""]
       [:pre {:class "source"}
        [:code {:class "clojure"}
         (-> entry :source :code)]]]]))

(defn api-entry
  "" [[var entry :as pair] {:keys [project namespace] :as elem}]
  [:div {:class "entry"}
   [:span {:id (entry-tag namespace var)}]
   [:div {:class "entry-description"}
    [:h4 [:b (str var) "&nbsp"
          [:a {:data-scroll ""
               :href (str "#" (entry-tag namespace ""))} "^"]]]
    [:p [:i (lower-first (:intro entry))]]]
   (api-entry-source entry project var namespace)
   (api-entry-example entry project)])

(defn api-element
  ""
  [{:keys [table tag title namespace only exclude project display]
    :or {display #{:tags :entries}}
    :as elem}]
  (let [entries (or (if only (map symbol only))
                    (->> (keys table)
                         (sort)
                         (remove (set (map symbol exclude)))))]
    [:div {:class "api"}
     [:span {:id (entry-tag namespace "")}]
     (if (not= "" title)
       [:div [:h2 (or title namespace)]])
     [:hr]
     [:div
      (if (display :tags)
        (apply vector
               :ul
               (map (fn [v]
                      [:li [:a {:data-scroll ""
                                :href (str "#" (entry-tag namespace v))}
                            (str v)]])
                    entries)))
      (if (= display #{:tags :entries})
        [:hr {:style "margin-bottom: 0"}])
      (if (display :entries)
          (apply vector
                 :div
                 (->> entries
                      (map (juxt identity table))
                      (map #(api-entry % elem)))))]]))
