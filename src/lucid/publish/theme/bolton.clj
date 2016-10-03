(ns lucid.publish.theme.bolton
  (:require [lucid.publish.engine :as engine]
            [lucid.publish.render.structure :as structure]
            [hiccup.compiler :as compiler]
            [hiccup.core :as html]
            [clojure.string :as string]))

(def settings
  {:engine    "winterfell"
   :resource  "theme/bolton"
   :copy      ["assets"]
   :render    {:article       "render-article"
               :outline       "render-outline"
               :top-level     "render-top-level"}
   :defaults  {:site           "bolton"
               :icon           "favicon"
               :tracking-enabled "false"
               :template       "article.html"
               :theme-base     "theme-base-08"
               :logo-white     "img/logo-white.png"
               :css-api        [:file "partials/api.css"]
               :css-bolton     [:file "partials/bolton.css"]
               :css-highlight  [:file "partials/highlight.css"]}
   :manifest  ["article.html"
               "home.html"
               "assets/favicon.ico"
               "assets/js/gumshoe.min.js"
               "assets/js/highlight.min.js"
               "assets/js/smooth-scroll.min.js"
               "assets/css/lanyon.css"
               "assets/css/poole.css"
               "assets/css/syntax.css"
               "assets/img/logo.png"
               "assets/img/logo-white.png"
               "partials/api.css"
               "partials/bolton.css"
               "partials/highlight.css"]})

(def engine (engine/engine (:engine settings)))

(defn render-top-level [interim name]
  (let [files (-> interim
                  :project
                  :publish
                  :files
                  (dissoc "index")
                  (sort))]
    (->> files
         (map (fn [[key {title :title}]]
                (html/html [:a {:class (str "sidebar-nav-item"
                                            (if (= name key)
                                              " active"))
                                :href (str key ".html")}
                            title])))
         (string/join))))

(defn render-article [interim name]
  (->> (get-in interim [:articles name :elements])
       (map (:page-element engine))
       (mapcat (fn [ele] (#'compiler/compile-seq [ele])))
       (string/join)))

(defn entry-tag [ns var]
  (.replaceAll (munge (str "entry__" ns "__" var)) "\\." "_"))

(defn render-chapter [{:keys [tag title number elements link table only exclude] :as elem}]
  (apply vector
         :li
         [:a {:class "chapter"
              :data-scroll ""
              :href (str "#" tag)}
          [:h4 (str number " &nbsp; " title)]]
         (cond (and link table)
               (let [entries (or (if only (map symbol only))
                                 (->> (keys table)
                                      (sort)
                                      (remove (set (map symbol exclude)))))]
                 (mapv (fn [entry]
                         [:a {:class "section"
                              :data-scroll ""
                              :href (str "#" (entry-tag link entry))}
                          [:h5 [:i (str entry)]]])
                       entries))

           :else
           (mapv (fn [{:keys [tag title number] :as elem}]
                   [:a {:class "section"
                        :data-scroll ""
                        :href (str "#" tag)}
                    [:h5 [:i (str number " &nbsp; " title)]]])
                 elements))))

(defn render-outline [interim name]
  (->> (get-in interim [:articles name :elements])
       (filter #(-> % :type #{:chapter :section}))
       structure/structure
       :elements
       (map render-chapter)
       (mapcat (fn [ele] (#'compiler/compile-seq [ele])))
       string/join))

(defn render-navigation [interim name]
  (let [elems (get-in interim [:articles name :elements])
        telems (filter #(-> % :type #{:chapter :section :subsection}) elems)]
    (->> telems
         (map (:nav-element engine))
         (mapcat (fn [ele] (#'compiler/compile-seq [ele])))
         string/join)))
