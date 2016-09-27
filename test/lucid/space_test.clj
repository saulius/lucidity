(ns lucid.space-test
  (:use hara.test)
  (:require [lucid.space :refer :all]
            [lucid.space.jar-test :refer [*match-path* *match-version*]]))

^{:refer lucid.space/coordinate :added "1.1"}
(fact "creates a coordinate based on the path"

  (coordinate *match-path*)
  => ['org.clojure/core.match *match-version*])

^{:refer lucid.space/coordinate-dependencies :added "1.1"}
(fact "list dependencies for a coordinate"

  (coordinate-dependencies [['org.clojure/core.match *match-version*]])
  => [])

^{:refer lucid.space/resolve-jar :added "1.1"}
(fact "resolves a jar according to context"

  (resolve-jar 'clojure.core.match)
  => [*match-path* "clojure/core/match.clj"])

^{:refer lucid.space/resolve-coordinates :added "1.1"}
(fact "resolves a set of coordinates"

  (resolve-coordinates 'clojure.core.match)
  => ['org.clojure/core.match *match-version*])

^{:refer lucid.space/resolve-with-dependencies :added "1.1"}
(fact "resolves the jar and path of a namespace"

  (resolve-with-dependencies 'clojure.core.match)
  => [*match-path* "clojure/core/match.clj"])

^{:refer lucid.space/pull :added "1.1"}
(fact "pulls down the necessary dependencies from maven and adds it to the project"

  (pull ['org.clojure/core.match *match-version*])
  => '[[org.ow2.asm/asm-all "4.1"]
       [org.clojure/tools.analyzer.jvm "0.1.0-beta12"]
       [org.clojure/tools.analyzer "0.1.0-beta12"]
       [org.clojure/data.priority-map "0.0.2"]
       [org.clojure/core.memoize "0.5.6"]
       [org.clojure/core.match "0.2.2"]
       [org.clojure/core.cache "0.6.3"]])
