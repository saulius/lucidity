(ns lucid.space.search
  (:require [hara.io.classloader :as cls]
            [lucid.space
             [file :as file]
             [jar :as jar]]))

(defn all-jars
  "gets all jars, either on the classloader or coordinate
 
   (-> (all-jars)
       count)
   => 150
 
   (-> (all-jars '[org.eclipse.aether/aether-api \"1.1.0\"])
       count)
   => 1"
  {:added "1.2"}
  [& [x :as coords]]
  (cond (nil? x)
        (->> (cls/delegation cls/+rt+)
             (mapcat #(.getURLs %))
             (map #(.getFile %))
             (filter #(.endsWith % ".jar")))

        :else
        (keep (fn [x]
                (if (vector? x)
                  (jar/maven-file x)
                  x))
              coords)))

(defn class-seq
  "creates a sequence of class names
 
   (-> (all-jars '[org.eclipse.aether/aether-api \"1.1.0\"])
       (class-seq)
       (count))
   => 128"
  {:added "1.2"}
  ([] (class-seq nil))
  ([coords]
   (->> (for [jar (apply all-jars coords)
              item (jar/jar-contents jar)]
          item)
        (filter #(.endsWith % ".class"))
        (map #(.substring % 0 (- (.length %) 6)))
        (map #(.replaceAll % "/" ".")))))

(defn search
  "searches a pattern for class names
 
   (->> (.getURLs cls/+base+)
        (map #(.getFile %))
        (filter #(.endsWith % \"jfxrt.jar\"))
        (apply search #\"^javafx.*[^\\]Builder$\")
        (take 10))
   => (javafx.animation.AnimationBuilder
      javafx.animation.FadeTransitionBuilder
       javafx.animation.FillTransitionBuilder
       javafx.animation.ParallelTransitionBuilder
       javafx.animation.PathTransitionBuilder
       javafx.animation.PauseTransitionBuilder
       javafx.animation.RotateTransitionBuilder
       javafx.animation.ScaleTransitionBuilder
       javafx.animation.SequentialTransitionBuilder
       javafx.animation.StrokeTransitionBuilder)"
  {:added "1.2"}
  [pattern & coords]
  (->> (class-seq coords)
       (filter #(re-find pattern %))
       (map #(Class/forName %))))
