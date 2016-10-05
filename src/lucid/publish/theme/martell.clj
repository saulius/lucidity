(ns lucid.publish.theme.martell
  (:require [lucid.publish.theme.martell
             [article :as article]
             [navigation :as navigation]]
            [hiccup.compiler :as compiler]
            [hiccup.core :as html]
            [clojure.string :as string]))

(def settings
  {:resource  "theme/martell"
   :copy      ["assets"]
   :structure true
   :render    {:article    "render-article"
               :navigation "render-navigation"
               :top-links  "render-top-links"}
   :defaults  {:template     "article.html"
               :sidebar      [:file "partials/sidebar.html"]
               :footer       [:file "partials/footer.html"]
               :dependencies [:file "partials/deps-web.html"]
               :icon        "favicon"
               :logo        "img/logo.png"}
   :manifest  ["article.html"
               "home.html"
               "assets/martell.ico"
               "assets/css/rdash.min.css"
               "assets/css/scrollspy.css"
               "assets/fonts/montserrat-regular-webfont.eot"
               "assets/fonts/montserrat-regular-webfont.svg"
               "assets/fonts/montserrat-regular-webfont.ttf"
               "assets/fonts/montserrat-regular-webfont.woff"
               "assets/img/glyph-color.png"
               "assets/img/glyph-white.png"
               "assets/img/logo.png"
               "assets/img/logo-white.png"
               "assets/js/angular-highlightjs.min.js"
               "partials/deps-local.html"
               "partials/deps-web.html"
               "partials/footer.html"
               "partials/sidebar.html"]})

(defn render-top-links
  ""
  [interim name]
  (let [files (-> interim
                  :project
                  :publish
                  :files
                  (dissoc "index")
                  (sort))]
    (->> files
         (map (fn [[key {title :title}]]
                [:li [:a {:href (str key ".html")} title]]))
         (concat [:ul {:class "dropdown-menu"}])
         vec
         html/html)))

(defn render-article
  "" [interim name]
  (->> (get-in interim [:articles name :elements :elements])
       (mapv #(article/render % interim))
       (mapcat (fn [ele] (#'compiler/compile-seq [ele])))
       (string/join)))

(defn render-navigation
  "" [interim name]
  (let [elements (get-in interim [:articles name :elements :elements])
        chapters (filter (fn [e] (#{:chapter :appendix} (:type e)))
                         elements)]
    (->> chapters
         (map #(navigation/render % interim))
         (#'compiler/compile-seq)
         (string/join))))
