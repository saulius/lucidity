(ns lucid.distribute.analyser.cljs-test
  (:use midje.sweet)
  (:require [lucid.distribute.analyser.cljs :refer :all]
            [lucid.distribute.analyser :as analyser]
            [clojure.java.io :as io]))

(fact "behavior of the cljs analyser"
  (analyser/file-info
   (io/file "example/repack.advance/src/cljs/repack/web.cljs"))
  => '{:exports #{[:cljs repack.web]}
       :imports #{[:cljs repack.web.client] [:clj repack.core]}})
