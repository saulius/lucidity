(ns lucid.distribute.analyser-test
  (:use hara.test)
  (:require [lucid.distribute.analyser :refer :all]
            [clojure.java.io :as io]))

^{:refer lucid.distribute.analyser/file-info :added "1.2"}
(fact "returns the analysis of source files"

  (file-info
   (io/file "example/distribute.advance/src/clj/repack/web/client.clj"))
  => '{:exports #{[:clj repack.web.client]
                  [:class repack.web.client.Main]
                  [:class repack.web.client.Client]}
       :imports #{[:clj repack.core]}}
  
  (file-info
   (io/file "example/distribute.advance/java/im/chit/repack/web/Client.java"))
  => (contains
      '{:exports #{[:class im.chit.repack.web.Client]}
        :imports #{[:class im.chit.repack.common.Hello]}})
  
  (file-info
   (io/file "example/distribute.advance/src/cljs/repack/web.cljs"))
  => (contains
      '{:exports #{[:cljs repack.web]}
        :imports #{[:cljs repack.web.client] [:clj repack.core]}}))
