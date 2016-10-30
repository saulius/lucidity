(ns lucid.distribute.split-test
  (:use hara.test)
  (:require [lucid.distribute.split :refer :all]
            [hara.io.project :as project]))

^{:refer lucid.distribute.split/interim-path :added "1.2"}
(comment "shows the interim path where the files will be split"

  (interim-path (project/project))
  ;;=> "/Users/chris/Development/chit/lucidity/target/interim"
  )

^{:refer lucid.distribute.split/copy-files :added "1.2"}
(comment "copies a set of relative paths from one place to another")

^{:refer lucid.distribute.split/split-scaffold :added "1.2"}
(comment "generates the scaffold for the split path")

^{:refer lucid.distribute.split/split-all-files :added "1.2"}
(comment "splits up the files in the manifest to various folders")

^{:refer lucid.distribute.split/split-project-files :added "1.2"}
(comment "creates the necessary project files for deployment")

^{:refer lucid.distribute.split/clean :added "1.2"}
(comment "deletes the interim directory"
         
  (clean (project/project))
  ;;=> deletes the `target/interim` directory
)

^{:refer lucid.distribute.split/split :added "1.2"}
(comment "splits up current project to put in the interim directory"
         
  (split (project/project))
  ;;=> look in `target/interim` for changes
  )


(comment
  (./import)
  (require '[lucid.distribute.manifest :as manifest])
  (def project (project/project "example/distribute.advance/project.clj"))
  (def manifest (manifest/create project))
  (clean project)
  (split project)

  (create-scaffold project manifest)
  (create-files project manifest)
  (create-project-clj-files project manifest))
