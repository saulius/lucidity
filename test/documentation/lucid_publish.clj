(ns documentation.lucid-publish
  (:use hara.test))

[[:chapter {:title "Introduction"}]]

"`lucid.publish` facilitates the creation of 'documentation that we can run', the tool allows for a design-orientated workflow for the programming process, blurring the boundaries between design, development, testing and documentation. This library was originally developed as [lein-midje-doc](https://www.github.com/zcaudate/lein-midje-doc), and then [hydrox](https://www.github.com/helpshift/hydrox)." 

[[:chapter {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.publish "{{PROJECT.version}}"])

"All functionality is in the `lucid.publish` namespace:"

(comment
  (use 'lucid.publish))

[[:section {:title "Motivation"}]]

"Documentation is the programmers' means of communicating how to use a library. There are various tools for documentation like `latex`, `wiki` and `markdown`- however, they are not linked to code and

1. To generate `.html` documentation from a `.clj` test file.
- Check code-examples for errors using test suites
- Render code and test cases as examples
- Latex-like numbering and linking facilities
- Code as Communication"



[[:chapter {:title "Design Driven Development"}]]

"Documentation at the design level requires more visual elements than documentation at the function level. `lucid.publish` can generates a `.html` output based on a `.clj` file. This requires some configuration and so the following is placed in the `project.clj` file:"

(comment
  (defproject ...
    ...
    :publish {:site   "sample"
              :output "docs"
              :paths ["test/documentation"]
              :files {"sample-document"
                      {:input "test/documentation/sample_document.clj"
                       :title "a sample document"
                       :subtitle "generating a document from code"}}}
    ...))

"The `:publish` key in `defproject` specifies which files to use as entry points to use for html generation. A sample can be seen below:"

(comment
  (ns documentation.sample.document
    (:require [midje.sweet :refer :all]))

  [[:chapter {:tag "hello" :title "Hello Midje Doc"}]]

  "This is an introduction to writing with midje-doc."

  [[:section {:title "Defining a function"}]]

  "We define function `add-5` below:"

  [[{:numbered false}]]
  (defn add-5 [x]
    (+ x 5))

  [[:section {:title "Testing a function"}]]

  "`add-5` outputs the following results:"

  (facts
    [[{:tag "add-5-1" :title "1 add 5 = 6"}]]
    (add-5 1) => 6

    [[{:tag "add-5-10" :title "10 add 5 = 15"}]]
    (add-5 10) => 15))

"A pretty looking html document can be generated by running `publish`:"

(comment
  (publish "sample-document"))

"The `:output` entry specifies the directory that files are rendered to."

[[:file {:src "test/documentation/lucid_publish/api.clj"}]]

[[:file {:src "test/documentation/lucid_publish/bug_example.clj"}]]
