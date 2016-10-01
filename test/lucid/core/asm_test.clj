(ns lucid.core.asm-test
  (:use hara.test)
  (:require [lucid.core.asm :refer :all]
            [hara.common.checks :as checks]
            [clojure.java.io :as io]))

^{:refer lucid.core.asm/to-bytes :added "1.1"}
(fact "opens `.class` file from an external source"
  (to-bytes "target/classes/test/Dog.class")
  => checks/bytes?)

^{:refer lucid.core.asm/path->classname :added "1.1"}
(fact "converts the path to a classname"
  (path->classname "test/Dog.class")
  => "test.Dog")

^{:refer lucid.core.asm/dynamic-loader :added "1.1"}
(fact "returns the clojure runtime classloader")

^{:refer lucid.core.asm/load-class :added "1.1"}
(fact "loads class from an external source")

^{:refer lucid.core.asm/unload-class :added "1.1"}
(fact "unloads class from the clojure runtime cache")