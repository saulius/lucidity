(ns lucid.distribute.manifest.classify-test
  (:use hara.test)
  (:require [lucid.distribute.manifest.classify :refer :all]))

^{:refer lucid.distribute.manifest.classify/name->path :added "1.2"}
(fact "transforms a name into a path"

  (name->path "lucid.distribute.manifest.classify-test")
  => "lucid/distribute/manifest/classify_test")

^{:refer lucid.distribute.manifest.classify/grab-namespaces :added "1.2"}
(fact "grabs all the namespaces in the ns declaration"

  (grab-namespaces '(:require lucid.distribute.analyser))
  => '(lucid.distribute.analyser)

  (grab-namespaces '(:require [lucid.distribute.manifest]))
  => '(lucid.distribute.manifest)

  (grab-namespaces '(:require [lucid.distribute.manifest :as manifest]))
  => '(lucid.distribute.manifest)
  
  (grab-namespaces '(:require [lucid.distribute analyser manifest]))
  => '(lucid.distribute.analyser
       lucid.distribute.manifest)

  (grab-namespaces '(:require [lucid.distribute
                               analyser
                               [manifest]]))
  => '(lucid.distribute.analyser
       lucid.distribute.manifest))

^{:refer lucid.distribute.manifest.classify/grab-classes :added "1.2"}
(fact "grabs all the classes in the ns declaration"

  (grab-classes '(:import clojure.lang.Atom))
  => '(clojure.lang.Atom)

  (grab-classes '(:import (clojure.lang Atom Ref)))
  => '(clojure.lang.Atom clojure.lang.Ref))
