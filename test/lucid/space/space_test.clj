(ns lucid.space.search-test
  (:use hara.test)
  (:require [lucid.space.search :refer :all]
            [hara.io.classloader :as cls]))

^{:refer lucid.space.search/all-jars :added "1.2"}
(comment  "gets all jars, either on the classloader or coordinate"

  (-> (all-jars)
      count)
  => 150

  (-> (all-jars '[org.eclipse.aether/aether-api "1.1.0"])
      count)
  => 1)

^{:refer lucid.space.search/class-seq :added "1.2"}
(fact "creates a sequence of class names"

  (-> (all-jars '[org.eclipse.aether/aether-api "1.1.0"])
      (class-seq)
      (count))
  => 128)

^{:refer lucid.space.search/search :added "1.2"}
(comment "searches a pattern for class names"

  (->> (.getURLs cls/+base+)
       (map #(.getFile %))
       (filter #(.endsWith % "jfxrt.jar"))
       (apply search #"^javafx.*[^\.]Builder$")
       (remove #(.startsWith % "javafx.fxml."))
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
      javafx.animation.StrokeTransitionBuilder))
