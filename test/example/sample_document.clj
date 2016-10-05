^{:name "example"
  :site "hello"
  :title "world"
  :subtitle "this is a sample document"}

(ns example.sample-document
  (:use hara.test)
  (:require [lucid.publish :as publish]))
  
[[:chapter {:tag "hello" :title "Introduction"}]]
  
"This is an introduction to writing with **Lucidity**"

[[:section {:title "Defining a function"}]]

"We define function `add-5` below:"

[[{:numbered false}]]
(defn add-5 [x]
  (+ x 5))

[[:section {:title "Testing a function"}]]

"`add-5` outputs the following results:"

[[{:tag "add-5-1" :title "1 add 5 = 6"}]]
(fact (add-5 1) => 6)

[[{:tag "add-5-10" :title "10 add 5 = 15"}]]
(fact (add-5 10) => 15)

[[:chapter {:tag "two" :title "Another Chapter"}]]

[[{:title "map"}]]
(comment
  (map inc (range 10))
  => (1 2 3 4 5 6 7 8 9 10)) 

[[{:hidden true}]]
(comment
  (publish/publish)
  
  )
