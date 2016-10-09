(ns lucid.publish.parse-test
  (:use hara.test)
  (:require [lucid.publish.parse :refer :all]
            [rewrite-clj.zip :as z]))

^{:refer lucid.publish.parse/parse-ns-form :added "1.2"}
(fact "converts a ns zipper into an element"

  (-> (z/of-string "(ns example.core)")
      (parse-ns-form))
  => {:type :ns-form
      :indentation 0
      :meta {}
      :ns 'example.core
      :code "(ns example.core)"})

^{:refer lucid.publish.parse/code-form :added "1.2"}
(fact "converts a form zipper into a code string"
  
  (-> (z/of-string "(fact (+ 1 1) \n => 2)")
      (code-form 'fact))
  => "(+ 1 1) \n => 2")

^{:refer lucid.publish.parse/parse-fact-form :added "1.2"}
(fact "convert a fact zipper into an element"

  (-> (z/of-string "(fact (+ 1 1) \n => 2)")
      (parse-fact-form))
  => {:type :test :indentation 2 :code "(+ 1 1) \n => 2"})

^{:refer lucid.publish.parse/parse-comment-form :added "1.2"}
(fact "convert a comment zipper into an element"

  (-> (z/of-string "(comment (+ 1 1) \n => 2)")
      (parse-comment-form))
  => {:type :block :indentation 2 :code "(+ 1 1) \n => 2"})

^{:refer lucid.publish.parse/parse-paragraph :added "1.2"}
(fact "converts a string zipper into an element"

  (-> (z/of-string "\"this is a paragraph\"")
      (parse-paragraph))
  => {:type :paragraph :text "this is a paragraph"})

^{:refer lucid.publish.parse/parse-directive :added "1.2"}
(fact "converts a directive zipper into an element"

  (-> (z/of-string "[[:chapter {:title \"hello world\"}]]")
      (parse-directive))
  => {:type :chapter :title "hello world"}

  (binding [*namespace* 'example.core]
    (-> (z/of-string "[[:ns {:title \"hello world\"}]]")
        (parse-directive)))
  => {:type :ns, :title "hello world", :ns 'example.core})

^{:refer lucid.publish.parse/parse-attribute :added "1.2"}
(fact "coverts an attribute zipper into an element"

  (-> (z/of-string "[[{:title \"hello world\"}]]")
      (parse-attribute))
  => {:type :attribute, :title "hello world"})

^{:refer lucid.publish.parse/parse-code-directive :added "1.2"}
(fact "coverts an code directive zipper into an element"

  (-> (z/of-string "[[:code {:language :ruby} \"1 + 1 == 2\"]]")
      (parse-code-directive))
  => {:type :block, :indentation 0 :code "1 + 1 == 2" :language :ruby})

^{:refer lucid.publish.parse/parse-whitespace :added "1.2"}
(fact "coverts a whitespace zipper into an element"

  (-> (z/of-string "1 2 3")
      (z/right*)
      (parse-whitespace))
  => {:type :whitespace, :code [" "]})

^{:refer lucid.publish.parse/parse-code :added "1.2"}
(fact "coverts a code zipper into an element"

  (-> (z/of-string "(+ 1 1) (+ 2 2)")
      (parse-code))
  => {:type :code, :indentation 0, :code ["(+ 1 1)"]})

^{:refer lucid.publish.parse/wrap-meta :added "1.2"}
(fact "if form is meta, then go down a level")

^{:refer lucid.publish.parse/parse-single :added "1.2"}
(fact "parse a single zloc")

^{:refer lucid.publish.parse/merge-current :added "1.2"}
(fact "if not whitespace, then merge output")

^{:refer lucid.publish.parse/parse-inner :added "1.2"}
(fact "parses the inner form of the fact and comment function")

^{:refer lucid.publish.parse/parse-loop :added "1.2"}
(fact "the main loop for the parser"

  (-> (z/of-string "(ns example.core) 
                    [[:chapter {:title \"hello\"}]] 
                    (+ 1 1) 
                    (+ 2 2)")
      (parse-loop {}))
  => [{:type :ns-form, :indentation 0, :ns 'example.core, :code "(ns example.core)"}
      {:type :chapter, :title "hello"}
      {:type :code, :indentation 0, :code ["(+ 1 1)"
                                           " "
                                           "\n"
                                           "                    "
                                           "(+ 2 2)"]}])
                                           
^{:refer lucid.publish.parse/parse-file :added "1.2"}
(fact "parse the entire file")
