(ns lucid.publish.engine.plugin.api
  (:require [lucid.publish.render.util :as util]
            [clojure.string :as string]))

(defn entry-tag [ns var]
  (munge (str "entry__" ns "__" var)))

(defn api-entry [[var entry] {:keys [project namespace] :as elem}]
  [:div {:class "entry"}
   [:a {:name (entry-tag namespace var)}]
   [:div {:class "entry-description"}
    [:h4 [:b (str var "&nbsp; &nbsp;")]]
    [:p [:i (:intro entry)]]]

   [:div {:class "entry-option"}
    [:h6
     "&nbsp;&nbsp;args: "
     [:i {:class "args"}
      (string/join " " (:arglists entry)) ]
     "&nbsp;&nbsp;&nbsp;"
     [:a {:href (format "%s/blob/master/%s#L%d-L%d"
                        (:url project)
                        (-> entry :source :path)
                        (-> entry :source :line :row)
                        (-> entry :source :line :end-row))
          :target "_blank"}
      "(source)"]]]
   (let [code (-> entry :test :code
                  (util/adjust-indent 2)
                  (string/trim))]
     [:pre
      [:h6 [:i [:a {:href (format "%s/blob/master/%s#L%d-L%d"
                                  (:url project)
                                  (-> entry :test :path)
                                  (-> entry :test :line :row)
                                  (-> entry :test :line :end-row))}
                "(test code)"]]]
      [:code {:class "clojure"} code]])])

(defn api-contents [{:keys [table project] :as elem}]
  (->> table
       seq
       sort
       (map #(api-entry % elem))))

(defn api-element
  [{:keys [table tag title namespace] :as elem}]
  [:div {:class "api"}
   [:div [:hr] [:h2 (or title namespace)] [:hr]]
   [:div
    [:h3 "Outline"]
    (apply vector
           :ul
           (map (fn [v]
                  [:li [:a {:href (str "#" (entry-tag namespace v))} (str v)]
                   " - " (get-in table [v :intro])])
                (-> table keys sort)))
    [:h3 "Contents"]
    (apply vector :div (api-contents elem))]])
