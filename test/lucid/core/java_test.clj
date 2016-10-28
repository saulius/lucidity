(ns lucid.core.java-test
  (:use hara.test)
  (:require [lucid.core.java :refer :all]
            [hara.io.project :as project]))

^{:refer lucid.core.java/path->class :added "1.2"}
(fact "Creates a class symbol from a file"

  (path->class "test/Dog.java")
  => 'test.Dog

  (path->class "test/Cat.class")
  => 'test.Cat)

^{:refer lucid.core.java/java-sources :added "1.2"}
(fact "lists source classes in a project"

  (-> (java-sources (project/project))
      (keys)
      (sort))
  => '[test.Cat test.Dog test.DogBuilder
       test.Person test.PersonBuilder test.Pet])

^{:refer lucid.core.java/javac-output :added "1.2"}
(fact "Shows output of compilation")

^{:refer lucid.core.java/javac :added "1.2"}
(comment "compiles classes using the built-in compiler"

  (javac 'test.Cat 'test.Dog)
  ;;=> outputs `.class` files in target directory
  )

^{:refer lucid.core.java/reimport :added "1.2"}
(comment "compiles and reimports java source code dynamically"

  (reimport 'test.Cat 'test.Dog)
  ;;=> (test.Cat test.Dog)
)
