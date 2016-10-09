(ns lucid.publish.prepare-test
  (:use hara.test)
  (:require [lucid.publish.prepare :refer :all]
            [lucid.publish :as publish]
            [hara.io.project :as project]))

^{:refer lucid.publish.prepare/lookup-meta :added "1.2"}
(fact "takes a key and looks up the associated meta information"
  (lookup-meta "index"
               (publish/add-lookup (project/project))
               (project/project))
  => {:template "home.html"
      :input "test/documentation/home_lucidity.clj"
      :title "lucidity"
      :subtitle "tools for code clarity"
      :name "index"})

^{:refer lucid.publish.prepare/prepare-single :added "1.2"}
(fact "processes a single meta to generate an interim structure"

  (prepare-single (lookup-meta "index"
                               (publish/add-lookup (project/project))
                               (project/project)))
  
  => (contains-in {:articles {"index" map?},
                   :global map?
                   :namespaces map?
                   :anchors-lu {"index" map?},
                   :anchors {"index" map?}}))

^{:refer lucid.publish.prepare/prepare :added "1.2"}
(fact "prepares an interim structure for many inputs")
