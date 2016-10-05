(ns lucid.publish.theme.stark
  (:require [lucid.publish.engine.winterfell :as engine]
            [lucid.publish.render.structure :as structure]
            [hiccup.compiler :as compiler]
            [hiccup.core :as html]
            [clojure.string :as string]))

(def settings
  {:engine    "winterfell"
   :resource  "theme/stark"
   :copy      ["assets"]
   :render    {:article       "render-article"
               :outline       "render-outline"
               :top-level     "render-top-level"}
   :defaults  {:site           "stark"
               :icon           "favicon"
               :tracking-enabled "false"
               :template       "article.html"
               :theme-base     "theme-base-0b"
               :logo-white     "img/logo-white.png"}
   :manifest  ["article.html"
               "home.html"
               "assets/favicon.ico"
               "assets/js/gumshoe.min.js"
               "assets/js/highlight.min.js"
               "assets/js/smooth-scroll.min.js"
               "assets/css/stark.css"
               "assets/css/stark-api.css"
               "assets/css/stark-highlight.css"
               "assets/css/lanyon.css"
               "assets/css/poole.css"
               "assets/css/syntax.css"
               "assets/img/logo.png"
               "assets/img/logo-white.png"]})

;;(def engine (engine/engine (:engine settings)))

(defn render-top-level
  "" [interim name]
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

(defn render-article
  "" [interim name]
  (->> (get-in interim [:articles name :elements])
       (map engine/page-element)
       (mapcat (fn [ele] (#'compiler/compile-seq [ele])))
       (string/join)))

(defn render-outline
  "" [interim name]
  (->> (get-in interim [:articles name :elements])
       (filter #(-> % :type #{:chapter :section}))
       structure/structure
       :elements
       (map engine/render-chapter)
       (mapcat (fn [ele] (#'compiler/compile-seq [ele])))
       string/join))
