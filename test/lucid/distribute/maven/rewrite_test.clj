(ns lucid.distribute.maven.rewrite-test
  (:use hara.test)
  (:require [lucid.distribute.maven.rewrite :refer :all]))

^{:refer lucid.distribute.maven.rewrite/project-zip :added "1.2"}
(comment "returns the zipper for a project file")

^{:refer lucid.distribute.maven.rewrite/replace-project-value :added "1.2"}
(comment "replaces the value in a project for a particular keys")

^{:refer lucid.distribute.maven.rewrite/remove-project-key :added "1.2"}
(comment "replaces the value in a project for a particular keys")

^{:refer lucid.distribute.maven.rewrite/add-project-key :added "1.2"}
(comment "replaces the value in a project for a particular keys")

^{:refer lucid.distribute.maven.rewrite/root-project-string :added "1.2"}
(comment "generates the `project.clj` for the root project")

^{:refer lucid.distribute.maven.rewrite/branch-project-string :added "1.2"}
(comment "generates `project.clj` for the branch projects")

