(ns lucid.core.classloader-test
  (:use hara.test)
  (:require [lucid.core.classloader :refer :all]
            [hara.common.checks :as checks]
            [clojure.java.io :as io]))

^{:refer lucid.core.classloader/to-bytes :added "1.1"}
(fact "opens `.class` file from an external source"
  (to-bytes "target/classes/test/Dog.class")
  => checks/bytes?)

^{:refer lucid.core.classloader/path->classname :added "1.1"}
(fact "converts the path to a classname"
  (path->classname "test/Dog.class")
  => "test.Dog")

^{:refer lucid.core.classloader/dynamic-loader :added "1.1"}
(fact "returns the clojure runtime classloader")

^{:refer lucid.core.classloader/load-class :added "1.1"}
(fact "loads class from an external source")

^{:refer lucid.core.classloader/unload-class :added "1.1"}
(fact "unloads class from the clojure runtime cache")