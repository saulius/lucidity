(ns lucid.publish.theme.martell.article
  (:require [lucid.publish.render
             [structure :as structure]]
            [lucid.core.code
             [util :as util]]
            [clojure.string :as string]
            [rewrite-clj.node :as node]))

(defmulti render (fn [element interim] (:type element)))

(defmethod render
  :html
  [{:keys [src] :as element} interim]
  src)

(defmethod render
  :paragraph
  [{:keys [text indentation] :as element} interim]
  [:div {:class :paragraph}
   (-> text
       (util/markup)
       (util/basic-html-unescape)
       (util/adjust-indent (or indentation 0)))])

(defmethod render
  :generic
  [{:keys [tag text elements]} interim]
  (vec (concat [:section]
               (map #(render % interim) elements))))

(defmethod render
  :chapter
  [{:keys [tag text number title elements] :as element} interim]
  (vec (concat [:section {:id tag :class :chapter}
                [:h2 {:class :chapter} (str number "  &nbsp;&nbsp; " title)]]
               (->> (structure/seperate #(= (:type %) :section) elements)
                    (map (fn [group]
                           (if (= :section (:type (first group)))
                             (render (first group) interim)
                             (vec (concat [:div {:class :group}]
                                          (map #(render % interim) group))))))))))

(defmethod render
  :appendix
  [{:keys [tag text number title elements]} interim]
  (vec (concat [:section {:id tag :class :chapter}
                [:h2 {:class :chapter} (str number "  &nbsp;&nbsp; " title)]]
               (map #(render % interim) elements))))

(defmethod render
  :section
  [{:keys [tag text number title elements]} interim]
  (vec (concat [:section {:id tag :class :section}
                [:h3 {:class :section} (str number "  &nbsp;&nbsp; " title)]]
               (map #(render % interim) elements))))

(defmethod render
  :subsection
  [{:keys [tag text number title elements]} interim]
  (vec (concat [:section {:id tag :class :subsection}
                [:h4 {:class :subsection} (str number "  &nbsp;&nbsp; " title)]]
               (map #(render % interim) elements))))

(defmethod render
  :subsubsection
  [{:keys [tag text number title elements]} interim]
  (vec (concat [:section {:id tag :class :subsubsection}
                [:h4 {:class :subsubsection} (str number "  &nbsp;&nbsp; " title)]]
               (map #(render % interim) elements))))

(defmethod render
  :code
  [{:keys [tag hidden text code indentation lang number title] :as element} interim]
  [:div {:class :code}
   (if tag [:a {:name tag}])
   (if number
     [:h5 (str "e." number
               (if title (str "  &nbsp;-&nbsp; " title)))])
   (if-not hidden
     [:div {:hljs :hljs :no-escape :no-escape :language (or lang :clojure)}
      (-> code
          (util/join)
          (util/basic-html-escape)
          (util/adjust-indent indentation)
          (string/trimr)
          (string/trim-newline))])])

(defmethod render
  :block
  [element interim]
  (render (assoc element :type :code) interim))

(defmethod render 
  :test
  [element interim]
  (render (assoc element :type :code) interim))

(defmethod render
  :image
  [{:keys [tag title text number] :as element} interim]
  [:div {:class :figure}
   (if tag [:a {:name tag}])
   [:div {:class "img"}
    [:img (dissoc element :number :type :tag :text :title)]]
   (if number
     [:h4 [:i (str "fig." number
                   (if title (str "  &nbsp;-&nbsp; " title)))]])])

(defmethod render
  :namespace
  [{:keys [mode] :as element} interim])

(defn render-api-index [namespace tag nsp]
  (->> nsp
       (map first)
       (map (fn [sym]
              [:a {:href (str "#" tag "--" sym)} (str sym)]))
       (#(interleave % (repeat "&nbsp;&nbsp;")))
       (apply vector :div [:a {:name tag}]
              [:h4 [:i "API"]])))

(defn render-api-elements [namespace tag nsp]
  (->> nsp
       (mapv (fn [[func data]]
               [:div
                [:a {:name (str tag "--" (name func))}]
                [:h4 (name func)
                 " " [:a {:href (str "#" tag)} "&#9652;"]]
                [:div {:hljs :hljs :no-escape :no-escape :language :clojure}
                 (-> (map node/string (:docs data))
                     (util/join)
                     (util/basic-html-escape)
                     (util/adjust-indent 2)
                     (string/trimr)
                     (string/trim-newline))]]))
       (apply vector :div)))

(defmethod render
  :api
  [{:keys [namespace tag] :as element} interim]
  (let [tag (or tag (str "api-" (.replaceAll ^String namespace "\\." "-")))
        nsp (-> interim
                 :references
                 (get (symbol namespace))
                 (->> (filter (fn [[_ data]] (:docs data)))
                      (sort-by first)))]
    [:div {:class :api}
     [:hr]
     (render-api-index namespace tag nsp)
     [:hr]
     (render-api-elements namespace tag nsp)]))
