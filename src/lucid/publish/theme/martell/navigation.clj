(ns lucid.publish.theme.martell.navigation)

(defmulti render
  "" (fn [element interim] (:type element)))

(defmethod render
  :chapter
  [{:keys [tag number title elements] :as element} interim]
  [:li [:a {:href (str "#" tag)} (str number "  &nbsp;&nbsp; " title)]
   (let [sections (filter (fn [e] (= :section (:type e))) elements)]
     (if-not (empty? sections)
       (vec (concat [:ul {:class :nav}]
                    (map #(render % interim) sections)))))])

(defmethod render
  :appendix
  [{:keys [tag number title elements]} interim]
  [:li [:a {:href (str "#" tag)} (str number "  &nbsp;&nbsp; " title)]
   (let [sections (filter (fn [e] (= :section (:type e))) elements)]
     (if-not (empty? sections)
       (vec (concat [:ul {:class :nav}]
                    (map #(render % interim) sections)))))])

(defmethod render
  :section
  [{:keys [tag number title]} interim]
  [:li [:a {:href (str "#" tag)} (str number "  &nbsp;&nbsp; " title)]])
