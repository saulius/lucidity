(ns lucid.publish.engine.winterfell
  (:require [lucid.publish.render.util :as util]
            [lucid.publish.engine.plugin.api :as api]
            [clojure.string :as string]))

(defmulti page-element :type)

(defmethod page-element :html
  [{:keys [src]}]
  src)

(defmethod page-element :block
  [elem]
  (page-element (assoc elem :type :code :origin :block)))

(defmethod page-element :ns
  [elem]
  (page-element (assoc elem :type :code :origin :ns)))

(defmethod page-element :test
  [elem]
  (page-element (assoc elem :type :code :origin :test)))

(defmethod page-element :reference
  [elem]
  (page-element (assoc elem :type :code :origin :reference)))

(defmethod page-element :chapter
  [{:keys [tag number title]}]
  [:div
   (if tag [:a {:name tag}])
   [:h2 [:b (str number " &nbsp;&nbsp; " title)]]])

(defmethod page-element :section
  [{:keys [tag number title]}]
  [:div
   (if tag [:a {:name tag}])
   [:h3 (str number " &nbsp;&nbsp; " title)]])

(defmethod page-element :subsection
  [{:keys [tag number title]}]
  [:div
   (if tag [:a {:name tag}])
   [:h3 [:i (str number " &nbsp;&nbsp; " title)]]])

(defmethod page-element :paragraph
  [{:keys [text]}]
  [:div (util/basic-html-unescape (util/markup text))])

(defmethod page-element :image
  [{:keys [tag number title] :as elem}]
  [:div {:class "figure"}
   (if tag [:a {:name tag}])
   (if number
     [:h4 [:i (str "fig."
                   number
                   (if title (str "  &nbsp;-&nbsp; " title)))]])
   [:div {:class "img"}
    [:img (dissoc elem :number :type :tag)]]
   [:p]])

(defmethod page-element :code
  [{:keys [tag number title code lang indentation failed path] :as elem}]
  [:div {:class "code"}
   (if tag [:a {:name tag}])
   (if number
     [:h4 [:i (str "e."
                   number
                   (if title (str "  &nbsp;-&nbsp; " title)))]])
   [:pre
    [:code {:class (or lang "clojure")}
     (-> code
         (util/join-string)
         (util/basic-html-escape)
         (util/adjust-indent indentation)
         (string/trim))]]
   (if failed
     (apply vector
            :div
            {:class "failed"}
            [:h4 (str "FAILED: " (count (:output failed)))]
            [:h4 (str "FILE: " path)]
            [:h4 (str "LINE: " (:line failed))]
            [:hr]
            (map (fn [{:keys [data form check code]}]
                   [:div
                    [:h5 "&nbsp;"]
                    [:h5 "Line: " (:line code)]
                    [:h5 "Expression: " (str form)]
                    [:h5 "Expected: " (str check)]
                    [:h5 "Actual: " (str data)]])
                 (:output failed))))])

(defmethod page-element :api
  [elem]
  (api/api-element elem))

(defmethod page-element :default
  [{:keys [line] :as elem}]
  (throw (Exception. (str "Cannot process element:" elem))))

(defmulti nav-element :type)

(defmethod nav-element :chapter
  [{:keys [tag number title]}]
  [:h4
   [:a {:href (str "#" tag)} (str number " &nbsp; " title)]])

(defmethod nav-element :section
  [{:keys [tag number title]}]
  [:h5 "&nbsp;&nbsp;"
   [:i [:a {:href (str "#" tag)} (str number " &nbsp; " title)]]])

(defmethod nav-element :subsection
  [{:keys [tag number title]}]
  [:h5 "&nbsp;&nbsp;&nbsp;&nbsp;"
   [:i [:a {:href (str "#" tag)} (str number " &nbsp; " title)]]])
