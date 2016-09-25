(ns lucid.publish.prepare-test
  (:use hara.test)
  (:require [lucid.publish.prepare :refer :all]))

^{:refer lucid.publish.prepare/lookup-meta :added "1.2"}
(fact "takes a key and looks up the associated meta information"
  (lookup-meta "index")
  => {:template "home.html",
      :title "lucidity",
      :subtitle "tools for clarity",
      :name "index"}

  (lookup-meta 'documentation.lucid-mind)
  => {:input "test/documentation/lucid_mind.clj",
      :title "mind",
      :subtitle "simple, contemplative reflection",
      :name "lucid-mind"})

^{:refer lucid.publish.prepare/prepare-single :added "1.2"}
(fact "processes a single meta to generate an interim structure"

  (prepare-single (lookup-meta "index"))
  => (contains-in {:articles {"index" map?},
                   :global map?
                   :namespaces map?
                   :anchors-lu {"index" map?},
                   :anchors {"index" map?}}))

^{:refer lucid.publish.prepare/prepare :added "1.2"}
(fact "prepares an interim structure for many inputs")
