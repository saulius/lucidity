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
(fact "returns the clojure runtime classloader"

  (dynamic-loader)
  => #(instance? clojure.lang.DynamicClassLoader %))

^{:refer lucid.core.asm/load-class :added "1.1"}
(comment "loads class from an external source"
         
  (load-class "target/classes/test/Cat.class")
  => test.Cat

  (load-class "<.m2>/org/yaml/snakeyaml/1.5/snakeyaml-1.5.jar"
            "org/yaml/snakeyaml/Dumper.class")
  => org.yaml.snakeyaml.Dumper

  (load-class '[org.yaml/snakeyaml "1.5"]
            "org/yaml/snakeyaml/Dumper.class")
  => org.yaml.snakeyaml.Dumper)

^{:refer lucid.core.asm/unload-class :added "1.1"}
(fact "unloads a class from the current namespace"

  (unload-class "test.Cat")
  ;; #object[java.lang.ref.SoftReference 0x10074132
  ;;         "java.lang.ref.SoftReference@10074132"]
  )
