(ns lucid.distribute.analyser
  (:require [clojure.string :as string]
            [lucid.distribute.analyser
             [base :as base]
             java
             clj
             cljs]))

(defn file-info
  "returns the analysis of source files
 
   (file-info
    (io/file \"example/distribute.advance/src/clj/repack/web/client.clj\"))
   => '{:exports #{[:clj repack.web.client]
                   [:class repack.web.client.Main]
                   [:class repack.web.client.Client]}
        :imports #{[:clj repack.core]}}
   
   (file-info
    (io/file \"example/distribute.advance/java/im/chit/repack/web/Client.java\"))
   => (contains '{:exports #{[:class im.chit.repack.web.Client]}
                  :imports #{[:class im.chit.repack.common.Hello]}})
   
   (file-info
    (io/file \"example/distribute.advance/src/cljs/repack/web.cljs\"))
   => '{:exports #{[:cljs repack.web]}
        :imports #{[:cljs repack.web.client] [:clj repack.core]}}"
  {:added "1.2"}
  [file]
  (base/file-info file))
