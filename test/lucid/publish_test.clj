(ns lucid.publish-test
  (:use hara.test)
  (:require [lucid.publish :refer :all]))

^{:refer lucid.publish/output-path :added "1.2"}
(fact "creates a path representing where the output files will go")

^{:refer lucid.publish/copy-assets :added "1.2"}
(fact "copies all theme assets into the output directory")

^{:refer lucid.publish/apply-with-options :added "1.2"}
(fact "applies a method with options, settings and project")

^{:refer lucid.publish/publish :added "1.2"}
(fact "publishes a document as an html")

^{:refer lucid.publish/publish-all :added "1.2"}
(fact "publishes all documents as html")
