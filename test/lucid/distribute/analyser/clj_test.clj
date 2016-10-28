(ns lucid.distribute.analyser.clj-test
  (:use hara.test)
  (:require [lucid.distribute.analyser.clj :refer :all]
            [lucid.distribute.analyser :as analyser]
            [clojure.java.io :as io]))

^{:refer lucid.distribute.analyser.clj/get-namespaces :added "1.2"}
(fact "gets the namespaces of a clojure s declaration"

  (get-namespaces '(:require repack.util.array
                             [repack.util.data]) [:use :require])
  => '(repack.util.array repack.util.data)

  (get-namespaces '(:require [repack.util.array :refer :all])
                  [:use :require])
  => '(repack.util.array)

  (get-namespaces '(:require [repack.util
                              [array :as array]
                              data]) [:use :require])
  => '(repack.util.array repack.util.data))

^{:refer lucid.distribute.analyser.clj/get-imports :added "1.2"}
(fact "gets the class imports of a clojure ns declaration"

  (get-imports '(:import java.lang.String
                         java.lang.Class))
  => '(java.lang.String java.lang.Class)

  (get-imports '(:import [java.lang String Class]))
  => '(java.lang.String java.lang.Class))

^{:refer lucid.distribute.analyser.clj/get-genclass :added "1.2"}
(fact "gets the gen-class of a clojure ns declaration"

  (get-genclass 'hello '[(:gen-class :name im.chit.hello.MyClass)])
  => '[im.chit.hello.MyClass]

  (get-genclass 'hello '[(:import im.chit.hello.MyClass)])
  => nil)

^{:refer lucid.distribute.analyser.clj/get-defclass :added "1.2"}
(fact "gets all the defclass and deftype definitions in a set of forms"

  (get-defclass 'hello '[(deftype Record [])
                         (defrecord Database [])])
  => '(hello.Record hello.Database))
