(ns lucid.publish.render
  (:require [hara.string.prose :as prose]
            [lucid.publish.render
             [article :as article]
             [navigation :as navigation]]
            [hiccup.compiler :as compiler]
            [clojure.string :as string]))

(defn render-article [{:keys [elements]} folio]
  (->> elements
       (mapv #(article/render % folio))
       (mapcat (fn [ele] (#'compiler/compile-seq [ele])))
       (string/join)))

(defn render-navigation [{:keys [elements]} folio]
  (let [chapters (filter (fn [e] (#{:chapter :appendix} (:type e)))
                         elements)]
    (->> chapters
         (map #(navigation/render % folio))
         (#'compiler/compile-seq)
         (string/join))))

(defn replace-template [template includes opts project]
  (reduce-kv (fn [^String html k v]
               (let [full  (str (:root project) "/" (-> opts :template :path) "/" (second v))
                     value (cond (string? v)
                                 v

                                 (vector? v)
                                 (cond (= (first v) :file)
                                       (slurp full)))]
                 (.replaceAll html
                              (str "<@=" (name k) ">")
                              (prose/escape-dollars value))))
             template
             includes))
