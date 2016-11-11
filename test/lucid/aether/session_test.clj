(ns lucid.aether.session-test
  (:use hara.test)
  (:require [lucid.aether.session :refer :all]
            [lucid.aether.system :as system]))
  
^{:refer lucid.aether.session/session :added "1.2"}
(fact "creates a session from a system:"

  (session (system/repository-system)
           {})
  => org.eclipse.aether.RepositorySystemSession)
