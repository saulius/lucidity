(ns lucid.publish.theme.stark
  (:require [lucid.publish.render
             [util :as util]]
            [hiccup.compiler :as compiler]
            [hiccup.core :as html]
            [clojure.string :as string]))
            
(def settings
  {:resource  "theme/stark"
   :copy      ["assets"]
   :render    {:article       "render-article"
               :navigation    "render-navigation"
               :top-level     "render-top-level"}
   :defaults  {:icon          "favicon"
               :template      "article.html"
               :css-code      [:file "partials/code.css"]
               :css-highlight [:file "partials/highlight.css"]
               :css-page      [:file "partials/page.css"]
               :js-scale      [:file "partials/scale.js"]}
   :manifest  ["article.html"
               "assets/favicon.ico"
               "assets/js/highlight.min.js"
               "partials/code.css"
               "partials/highlight.css"
               "partials/page.css"
               "partials/scale.js"]})

(defn render-top-level [interim name]
  (let [files (-> interim
                  :project
                  :publish
                  :files
                  (dissoc "index")
                  (sort))]
    (->> files
         (map (fn [[key {title :title}]]
                [:li [:a {:href (str key ".html")} title]]))
         (concat [:ul [:li [:a {:href "index.html"} "home"]]])
         vec
         html/html)))

(defn render-element [{:keys [type number hidden name text tag
                              title code lang indentation src]
                       :as elem}]
  (if-not hidden
    (condp = type

      :html src
      
      :block
      (render-element (assoc elem :type :code))

      :test
      (render-element (assoc elem :type :code))
      
      :chapter
      [:div
       (if tag [:a {:name tag}])
       [:h2 [:b (str number " &nbsp;&nbsp; " title)]]]
      
      :section
      [:div
       (if tag [:a {:name tag}])
       [:h3 (str number " &nbsp;&nbsp; " title)]]
      
      :subsection
      [:div
       (if tag [:a {:name tag}])
       [:h3 [:i (str number " &nbsp;&nbsp; " title)]]]
      
      :subsubsection
      [:div
       (if tag [:a {:name tag}])
       [:h3 [:i (str number " &nbsp;&nbsp; " title)]]]
      
      :paragraph [:div (util/basic-html-unescape (util/markup text))]

      :image
      [:div {:class "figure"}
       (if tag [:a {:name tag}])
       (if number
         [:h4 [:i (str "fig." number
                       (if-let [t title] (str "  &nbsp;-&nbsp; " t)))]])
       [:div {:class "img"} [:img (dissoc elem :num :type :tag)]]
       [:p]]

      :ns
      [:div
       [:pre (-> elem :content util/basic-html-escape)]]

      :code
      [:div
       (if tag [:a {:name tag}])
       (if number
         [:h4 [:i (str "e." number
                       (if-let [t title] (str "  &nbsp;-&nbsp; " t)))]])
       [:pre [:code {:class (or lang "clojure")}
              (-> code
                  (util/join)
                  (util/basic-html-escape)
                  (util/adjust-indent indentation)
                  (string/triml)
                  (string/trimr)
                  (string/trim-newline))]]])))

(defn render-article [interim name]
  (->> (get-in interim [:articles name :elements])
       (map render-element)
       (mapcat (fn [ele] (#'compiler/compile-seq [ele])))
       (string/join)))

(defn render-navigation-element [{:keys [number tag title type] :as elem}]
  (case type
    :chapter [:h4
              [:a {:href (str "#" tag)} (str number " &nbsp; " title)]]

    :section [:h5 "&nbsp;&nbsp;"
              [:i [:a {:href (str "#" tag)} (str number " &nbsp; " title)]]]

    :subsection [:h5 "&nbsp;&nbsp;&nbsp;&nbsp;"
                [:i [:a {:href (str "#" tag)} (str number " &nbsp; " title)]]]))

(defn render-navigation [interim name]
  (let [elems (get-in interim [:articles name :elements])
        telems (filter #(-> % :type #{:chapter :section :subsection}) elems)]
    (->> (map render-navigation-element telems)
         (mapcat (fn [ele] (#'compiler/compile-seq [ele])))
         string/join)))
