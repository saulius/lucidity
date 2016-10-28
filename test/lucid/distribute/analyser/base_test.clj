(ns lucid.distribute.analyser.base-test
  (:use hara.test)
  (:require [lucid.distribute.analyser.base :refer :all]
            [lucid.distribute.analyser :as analyser]))
  
^{:refer lucid.distribute.analyser.base/file-type :added "1.2"}
(fact "encodes the type of file as a keyword"

  (file-type "hello.clj")
  => :clj

  (file-type "hello.java")
  => :java)

^{:refer lucid.distribute.analyser.base/file-info :added "1.2"}
(fact "returns the file-info"

  (file-info "src/lucid/distribute/analyser.clj")
  => '{:exports #{[:clj lucid.distribute.analyser]},
       :imports #{[:clj lucid.distribute.analyser.java]
                  [:clj lucid.distribute.analyser.cljs]
                  [:clj lucid.distribute.analyser.base]
                  [:clj clojure.string]
                  [:clj lucid.distribute.analyser.clj]}})
