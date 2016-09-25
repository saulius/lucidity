(ns lucid.theme.martell
  (:require [lucid.theme.martell
             [article :as article]
             [navigation :as navigation]]
            [hiccup.compiler :as compiler]
            [clojure.string :as string]))

(def settings
  {:resource "html/martell"
   :copy     ["assets"]
   :render   {:article    "render-article"
              :navigation "render-navigation"}
   :defaults {:template     "article.html"
              :navbar       [:file "partials/navbar.html"]
              :sidebar      [:file "partials/sidebar.html"]
              :footer       [:file "partials/footer.html"]
              :dependencies [:file "partials/deps-web.html"]}
   :manifest ["article.html"
              "home.html"
              "assets/css/rdash.min.css"
              "assets/css/scrollspy.css"
              "assets/fonts/montserrat-regular-webfont.eot"
              "assets/fonts/montserrat-regular-webfont.svg"
              "assets/fonts/montserrat-regular-webfont.ttf"
              "assets/fonts/montserrat-regular-webfont.woff"
              "assets/js/angular-highlightjs.min.js"
              "partials/deps-local.html"
              "partials/deps-web.html"
              "partials/footer.html"
              "partials/navbar.html"
              "partials/sidebar.html"]})

(defn render-article [interim name]
  (->> (get-in interim [:articles name :elements])
       (mapv #(article/render % interim))
       (mapcat (fn [ele] (#'compiler/compile-seq [ele])))
       (string/join)))

(defn render-navigation [interim name]
  (let [elements (get-in interim [:articles name :elements])
        chapters (filter (fn [e] (#{:chapter :appendix} (:type e)))
                         elements)]
    (->> chapters
         (map #(navigation/render % interim))
         (#'compiler/compile-seq)
         (string/join))))
