(ns lucid.distribute.util.rewrite-test
  (:use hara.test)
  (:require [lucid.distribute.util.rewrite :refer :all]))

^{:refer lucid.distribute.util.rewrite/project-zip :added "1.2"}
(comment "returns the zipper for a project file")

^{:refer lucid.distribute.util.rewrite/replace-project-value :added "1.2"}
(comment "replaces the value in a project for a particular keys")

^{:refer lucid.distribute.util.rewrite/remove-project-key :added "1.2"}
(comment "replaces the value in a project for a particular keys")

^{:refer lucid.distribute.util.rewrite/add-project-key :added "1.2"}
(comment "replaces the value in a project for a particular keys")

^{:refer lucid.distribute.util.rewrite/root-project-string :added "1.2"}
(comment "generates the `project.clj` for the root project")

^{:refer lucid.distribute.util.rewrite/branch-project-string :added "1.2"}
(comment "generates `project.clj` for the branch projects")

