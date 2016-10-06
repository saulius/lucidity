(ns lucid.publish.collect.base-test
  (:use hara.test)
  (:require [lucid.publish.collect.base :refer :all]))

^{:refer lucid.publish.collect.base/collect-namespaces :added "1.2"}
(fact "combines `:ns-form` directives into a namespace map for easy referral"
  
  (collect-namespaces
   {:articles
    {"example"
     {:elements [{:type :ns-form
                  :ns    'clojure.core}]}}}
   "example")
  => '{:articles {"example" {:elements ()}}
       :namespaces {clojure.core {:type :ns-form :ns clojure.core}}})

^{:refer lucid.publish.collect.base/collect-article :added "1.2"}
(fact "shunts `:article` directives into a seperate `:meta` section"
  
  (collect-article
   {:articles {"example" {:elements [{:type :article
                                      :options {:color :light}}]}}}
   "example")
  => '{:articles {"example" {:elements []
                             :meta {:options {:color :light}}}}})

^{:refer lucid.publish.collect.base/collect-global :added "1.2"}
(fact "shunts `:global` directives into a globally available `:meta` section"
  
  (collect-global
   {:articles {"example" {:elements [{:type :global
                                      :options {:color :light}}]}}}
   "example")
  => {:articles {"example" {:elements ()}}
      :meta {:options {:color :light}}})

^{:refer lucid.publish.collect.base/collect-tags :added "1.2"}
(fact "puts any element with `:tag` attribute into a seperate `:tag` set"
  
  (collect-tags
   {:articles {"example" {:elements [{:type :chapter :tag  "hello"}
                                     {:type :chapter :tag  "world"}]}}}
   "example")
  => {:articles {"example" {:elements [{:type :chapter :tag "hello"}
                                       {:type :chapter :tag "world"}]
                            :tags #{"hello" "world"}}}})

^{:refer lucid.publish.collect.base/collect-citations :added "1.2"}
(fact "shunts `:citation` directives into a seperate `:citation` section"
  
  (collect-citations
   {:articles {"example" {:elements [{:type :citation :author "Chris"}]}}}
   "example")
  => {:articles {"example" {:elements [],
                            :citations [{:type :citation, :author "Chris"}]}}})

;;(lucid.unit/import)
