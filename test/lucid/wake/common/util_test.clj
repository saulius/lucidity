(ns lucid.wake.common.util-test
  (:use hara.test)
  (:require [lucid.wake.common.util :refer :all]
            [clojure.java.io :as io]))

^{:refer lucid.wake.common.util/full-path :added "1.1"}
(fact "constructs a path from a project"

  (full-path "example/file.clj" "src" {:root "/home/user"})
  => "/home/user/src/example/file.clj")

^{:refer lucid.wake.common.util/filter-pred :added "1.1"}
(fact "filters values of a map that fits the predicate"
  (filter-pred string? {:a "valid" :b 0})
  => {:a "valid"})

^{:refer lucid.wake.common.util/escape-dollars :added "1.1"}
(fact "for regex purposes, escape dollar signs in templates")

^{:refer lucid.wake.common.util/read-project :added "1.1"}
(fact "like `leiningen.core.project/read` but with less features'"

  (keys (read-project (io/file "example/project.clj")))
  => (just [:description :license :name :source-paths :test-paths
            :documentation :root :url :version :dependencies] :in-any-order))

