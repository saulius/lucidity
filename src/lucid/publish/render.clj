(ns lucid.publish.render
  (:require [hara.io
             [project :as project]
             [file :as fs]]
            [hara.string
             [prose :as prose]]
            [clojure.java.io :as io]
            [lucid.publish
             [structure :as structure]
             [template :as template]]))

(defn find-includes
  "finds elements with `@=` tags

   (find-includes \"<@=hello> <@=world>\")
   => #{:hello :world}"
  {:added "0.1"}
  [template]
  (->> template
       (re-seq #"<@=([^>^<]+)>")
       (map second)
       (map keyword)
       set))

(defn prepare-template
  ([interim name]
   (let [project (project/project)
         theme  (-> project :publish :template :theme)
         settings (template/load-settings theme project)]
     (prepare-template interim name settings project)))
  ([interim name settings project]
   (let [defaults (:defaults settings)
         opts (merge defaults
                     (get-in interim [:articles name :meta]))
         template (template/load-file (:template opts) settings project)
         includes (find-includes template)]
     [template (select-keys opts includes)])))

(defn render
  ([interim name]
   (let [project (project/project)
         theme  (-> project :publish :template :theme)
         settings (template/load-settings theme project)]
     (render interim name settings project)))
  ([interim name settings project]
   (let [[template includes] (prepare-template interim name settings project)
         interim (update-in interim
                            [:articles name :elements]
                            structure/structure)]
     (reduce-kv (fn [^String html k v]
                  (let [value (cond (string? v) v

                                    (vector? v)
                                    (case (first v)
                                      :file (template/load-file (second v) settings project)
                                      :fn   ((second v) interim name)))]
                    (.replaceAll html
                                 (str "<@=" (clojure.core/name k) ">")
                                 (prose/escape-dollars value))))
                template
                includes))))

(comment
  (prepare-template
   (lucid.publish.prepare/prepare ["index"])
   "index")
  
  (render
   (lucid.publish.prepare/prepare ["index"])
   "index"))
