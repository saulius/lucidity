(ns lucid.aether.system-test
  (:use hara.test)
  (:require [lucid.aether.system :refer :all]))
  
^{:refer lucid.aether.system/repository-system :added "1.2"}
(fact "creates a repository system for interfacting with maven"

  (repository-system)
  => org.eclipse.aether.RepositorySystem)
