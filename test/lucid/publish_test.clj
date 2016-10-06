(ns lucid.publish-test
  (:use hara.test)
  (:require [lucid.publish :refer :all]))

^{:refer lucid.publish/output-path :added "1.2"}
(fact "creates a path representing where the output files will go")

^{:refer lucid.publish/copy-assets :added "1.2"}
(comment "copies all theme assets into the output directory"
         
  (copy-assets)
  ;; copies theme using the `:copy` key into an output directory
)

^{:refer lucid.publish/load-settings :added "1.2"}
(comment "copies all theme assets into the output directory"

  (load-settings)
  ;; {:email "z@caudate.me", :date "06 October 2016" ...}
)

^{:refer lucid.publish/add-lookup :added "1.2"}
(fact "adds a namespace to file lookup table if not existing")

^{:refer lucid.publish/apply-with-options :added "1.2"}
(fact "applies a method with options, settings and project")

^{:refer lucid.publish/publish :added "1.2"}
(comment "publishes a document as an html"
  
  (publish "index")
  ;; publishes the `index` entry in `project.clj`

  (publish "index"
           {:refresh true :theme "bolton"}
           (project/project <PATH>))
  ;; publishes `index` in a specific project with additional options
)

^{:refer lucid.publish/publish-all :added "1.2"}
(comment "publishes all documents as html"

  (publish-all)
  ;; publishes all the documents

  (publish-all {:refresh true :theme "bolton"}
               (project/project <PATH>))
  ;; publishes a specific project with additional options
  )
