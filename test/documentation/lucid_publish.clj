(ns documentation.lucid-publish
  (:use hara.test)
  (:require [lucid.publish :as publish]))

[[:chapter {:title "Introduction"}]]

"`lucid.publish` facilitates the creation of 'documentation that we can run', the tool allows for a design-orientated workflow for the programming process, blurring the boundaries between design, development, testing and documentation. This library was originally developed as [lein-midje-doc](https://www.github.com/zcaudate/lein-midje-doc), and then [hydrox](https://www.github.com/helpshift/hydrox)." 

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.publish "{{PROJECT.version}}"])

"All functionality is in the `lucid.publish` namespace:"

(comment
  (use 'lucid.publish))

[[:section {:title "Motivation"}]]

"Documentation is the programmers' means of communicating how to use a library. There are various tools for documentation like `latex`, `wiki` and `markdown`, however `lucid.publish` should be thought of as an in-built html renderer for clojure code. It's features are:

1. To generate `.html` documentation from a `.clj` test file.
- Render code and test cases as examples
- Latex-like numbering and linking facilities
- To promote code as communication"

[[:section {:title "Walkthrough"}]]

"Any clojure file can be made into a `.html` page. Let's give it a go. Create a file in `test/example/sample_document.clj` and copy and paste the following:"

(comment
  ^{:site "hello"
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
    (publish/publish)))

"Now either `C-x`-`C-e` on the `(publish/publish)` line or write in the repl:"

(comment
  (require '[lucid.publish :as publish])

  (publish/publish 'example.sample-document))

"The output will look something like <a href='example.sample-document.html' target='_blank'>this</a>"

[[:chapter {:title "API"
            :link "lucid.publish"
            :only ["copy-assets" "load-settings" "publish" "publish-all"]}]]

[[:api {:title ""
        :namespace "lucid.publish"
        :only ["copy-assets" "load-settings" "publish" "publish-all"]}]]


[[:chapter {:title "Syntax"}]]

"Elements are constructed using a tag and a map contained within double square brackets. Elements tags have been inspired from latex:
- [Content Elements](#content)
- [Sectioning Elements](#sections)
- [Code Elements](#code)

Clojure strings are treated as paragraph elements whilst clojure forms are treated as code elements. `fact` and `comment` forms are also considered code elements. Elements will be described in detail in their respective sections."


[[:section {:title "Notation"}]]


[[{:title "Element Notation" :tag "elements-ex1"}]]
(comment
  [[<tag> {<key1> <value1>, <key2> <value2>}]])

"for example:"

(comment
  [[:chapter {:title "Hello World" :tag "hello"}]])

[[:section {:title "Attributes"}]]

"Attribute add additional metadata to elements. They are written as a single hashmap within double square brackets. Attributes mean nothing by themselves. They change the properties of elements directly after them."

(comment
  [[{:tag "my-paragraph"}]]
  [[:paragraph {:content "This is a paragraph"}]])

"is equivalent to:"

(comment
  [[:paragraph {:content "This is a paragraph"
                :tag "my-paragraph"}]])


"Multiple attributes can be stacked to modify an element:"

(comment
  [[{:numbered false}]]
  [[{:lang "shell"}]]
  (comment
    > lein repl))

"displays the following:"

[[{:lang "shell" :numbered false}]]
(comment
    > lein repl)

[[:chapter {:title "Content"}]]

"Content elements include `:paragraph`, `:image`, and `file` elements."

[[:section {:title ":paragraph" :tag "paragraph"}]]

"Paragraph elements should make up the bulk of the documentation. They can be written as an element or in the usual case, as a string. The string is markdown with templating - so that chapter, section, code and image numbers can be referred to by their tags."

[[{:title "Paragraph Element"}]]
(comment
  [[:paragraph {:content "Here is some content"}]])

[[{:title "Paragraph String"}]]
(comment "Here is some content")


[[{:title "Markdown String"}]]
(comment
  [[:chapter {:title "Chapter Heading" :tag "ch-heading"}]]

  "
# Heading One
Here is some text.
Here is a tag reference to Chapter Heading - {{ch-heading}}

- Here is a bullet point
- Here is another one")


[[:section {:title "sections"}]]

"Sectioning elements are taken from latex and allow the document to be organised into logical sections. From highest to lowest order of priority, they are: `:chapter`, `section`, `subsection` and `:subsubsection`, giving four levels of organisation.

The numbering for elements are generated in sequencial order: `(1, 2, 3 ... etc)` and a tag can be generated from the title or specified for creating links within the document. `:chapter`, `section` and `subsection` elements are list in the table of contents using tags.

For example, I wish to write a chapter about animals and have organised content into categories shown below."

(comment
  Animals
  - Mammals
  - Birds
  - - Can Fly
  - - - Eagle
  - - - Hummingbird
  - - Flightless
  - - - Penguin)

"It is very straight forward to turn this into sectioning elements which will then generate the sectioning numbers for different categories"

(comment
  [[:chapter {:title "Animals"}]]
  [[:section {:title "Mammals"}]]
  [[:section {:title "Birds"}]]
  [[:subsection {:title "Can Fly"}]]
  [[:subsubsection {:title "Eagle"}]]
  [[:subsubsection {:title "Hummingbird"}]]
  [[:subsection {:title "Flightless"}]]
  [[:subsubsection {:title "Penguin"}]])

"The sections will be automatically numbered as show below:"

(comment
  Animals             ; 1 
  - Mammals           ; 1.1
  - Birds             ; 1.2
  - - Can Fly         ; 1.2.1
  - - - Eagle         ; 1.2.1.1
  - - - Hummingbird   ; 1.2.1.2
  - - Flightless      ; 1.2.2
  - - - Penguin       ; 1.2.2.1
  )


[[:section {:title ":image"}]]

"The `:image` element embeds an image as a figure within the document. It is numbered and can be tagged for easy reference. The code example below:"

(comment
  [[:image {:tag "clojure-logo" :title "Clojure Logo (source clojure.org)"
            :src "http://clojure.org/images/clojure-logo-120b.png"}]])

"produces the image below in Figure {{clojure-logo}}:"

[[:image {:tag "clojure-logo" :title "Clojure Logo (source clojure.org)"
          :src "http://clojure.org/images/clojure-logo-120b.png"}]]

[[:section {:title ":file"}]]

"The `:file` element allows inclusion of other files into the document. It is useful for breaking up a document into managable chunks. A file element require that the `:src` attribute be specified. A high-level view of a document can thus be achieved, making the source more readable. This is similar to the `\\include` element in latex."

[[{:tag "file-element" :title ":file tag example"}]]
(comment
  [[:file {:src "test/docs/first_section.clj"}]]
  [[:file {:src "test/docs/second_section.clj"}]]
  [[:file {:src "test/docs/third_section.clj"}]])

[[:chapter {:title "Code"}]]

"Code displayed in documentation are of a few types:

1. Code that needs to be run (normal clojure code)
- Code that needs verification taking input and showing output. (midje fact)
- Code that should not be run (namespace declaration examples)
- Code that is part of the library's tests or source definition
- Code in other languages

The different types of code can be defined so that code examples render properly using a variety of methods
"

[[:section {:title "normal s-expressions"}]]
"Normal s-expressions are rendered as is. Attributes can be added for grouping purposes. The source code shown below"

[[{:title "seperating code blocks through attributes" :tag "c-add-src"}]]
(comment
  [[{:title "add-n definition" :tag "c-add-1"}]]
  (defn add-n [n]
    (fn [x] (+ x n)))

  [[{:title "add-4 and add-5 definitions" :tag "c-add-2"}]]
  (def add-4 (add-n 4))
  (def add-5 (add-n 5)))

"renders the following outputs:"

[[{:title "add-n definition" :tag "c-add-1"}]]
(defn add-n [n]
  (fn [x] (+ x n)))

[[{:title "add-4 and add-5 definitions" :tag "c-add-2"}]]
(def add-4 (add-n 4))
(def add-5 (add-n 5))


[[:section {:title "fact/facts"}]]

"
Documentation examples put in `fact` forms allows the code to be verified for correctness using `hara.test` as well as `midje`."

[[{:tag "c-fact-src" :title "Fact Form Source"}]]
(comment
  [[{:tag "fact-form-output" :title "Fact Form Output"}]]
  (fact
    (def a (atom 1))
    (deref a) => 1

    (swap! a inc 1)
    (deref a) => 2))

"renders the this output:"

[[{:title "Fact Form Output"}]]
(fact
  (def a (atom 1))
  (deref a) => 1

  (swap! a inc)
  (deref a) => 2)

[[:section {:title "comment"}]]

"Comments are clojure's built-in method of displaying non-running code and so this mechanisim is used in clojure for displaying code that should not be run, but still requires display. Code can still output without interferring with code or tests."

[[{:title "Switching to a new namespace" :tag "c-com-src"}]]
(comment
  [[{:title "Switching to a new namespace" :tag "c-com-1"}]]
  (comment
    (in-ns 'hello.world)
    (use 'clojure.string)
    (split "Hello World" #"\s") ;=> ["Hello" "World"]
    ))

[[{:title "Switching to a new namespace" :tag "c-com-1"}]]
(comment
  (in-ns 'hello.world)
  (use 'clojure.string)
  (split "Hello World" #"\s") ;=> ["Hello" "World"]
  )

[[:section {:title ":reference"}]]

"Code in the repository can be directly referenced:"

(comment
  [[:reference {:refer "lucid.publish.theme/deploy"}]]
  )

"Gives this output:"

[[:reference {:refer "lucid.publish.theme/deploy" :title "Source Output" :tag "source-1"}]]


"Tests can be referred to by adding `:mode :test` to "

(comment
  [[:reference {:refer "lucid.publish.theme/deploy" :mode :test}]]
  )

[[:reference {:refer "lucid.publish.theme/deploy" :mode :test}  :title "Test Output" :tag "source-1"]]

[[:section {:title ":api"}]]

"An API table can be constructed using:"

(comment
  [[:api {:namespace "lucid.publish.theme"}]]
  )

"The above output will construct a table as shown below:"

[[:api {:namespace "lucid.publish.theme" :title "OUTPUT - lucid.publish.theme"}]]

"The table can be customised with `:title`, `:only` and `:exclude` keys. The following generates a table with only an entry for deploy:"

(comment
  [[:api {:namespace "lucid.publish.theme"
          :title ""
          :only ["deploy"]}]])

"This example generates a table with "

(comment
  [[:api {:namespace "lucid.publish.theme"
          :title ""
          :exclude ["template-path" "apply-settings"]}]])

"To make links to a `:chapter`, use `:link` to create navigation links for the api"

(comment
  [[:chapter {:namespace "Theme API"
              :link "lucid.publish.theme"
              :exclude ["template-path" "apply-settings"]}]]
  
  [[:api {:namespace "lucid.publish.theme"
          :title ""
          :exclude ["template-path" "apply-settings"]}]])

[[:section {:title "other languages"}]]

"The most generic way of displaying code is with the `:code` tag. It is useful when code in other languages are required to be in the documentation."

[[:subsection {:title "python"}]]

"The source and outputs are listed below:"

[[{:title "Python for Loop Source" :tag "c-py-src"}]]
(comment
  [[:code {:lang "python" :title "Python for Loop" :tag "c-py-1"}
  "
  myList = [1,2,3,4]
  for index in range(len(myList)):
    myList[index] += 1
  print myList"]])

[[:code {:lang "python" :title "Python for Loop" :tag "c-py-1"}
"
myList = [1,2,3,4]
for index in range(len(myList)):
  myList[index] += 1
print myList
"]]

[[:subsection {:title "ruby"}]]

"The source and outputs are listed below:"

[[{:title "Ruby for Loop Source" :tag "c-rb-src"}]]
(comment
  [[:code {:lang "ruby" :title "Ruby for Loop" :tag "c-rb-2"}
  "
  array.each_with_index do |element,index|
    element.do_stuff(index)
  end"]])

[[:code {:lang "ruby" :title "Ruby for Loop" :tag "c-rb-2"}
"
array.each_with_index do |element,index|
    element.do_stuff(index)
end"]]


[[:chapter {:title "Project Level"}]]

[[:section {:title "Setup"}]]

"`lucid.publish` can also be used to generate an entire site based on clojure code input. The default template can be changed according to need. `lucid.publish` can generates a `.html` output based on a `.clj` file. The `:publish` key in `defproject` specifies which files to use as entry points to use for html generation. A sample can be seen below:"

(comment
  (defproject ...
    ...
    :publish {:site   "sample"
              :theme  "bolton" ;; stark is the default
              :output "docs"
              :files {"sample-document"
                      {:input "test/documentation/sample_document.clj"
                       :title "a sample document"
                       :subtitle "generating a document from code"}}}
    ...))

"This is the simplest example - more options can be added as needed. See examples for [hara](https://github.com/zcaudate/hara/blob/master/project.clj#L23-L31) and [lucidity](https://github.com/zcaudate/lucidity/blob/master/project.clj#L46-L55)"

[[:section {:title "Templates and Themes"}]]

"Templates can be set up to customise the site to look however fancy it needs to be."

[[:code {:lang "html" :title "Template"}
"
<html>
  ...
  <head>
    <title><@=title></title>
  </head>

  <body>
    <@=navigation>
    <@=contents>
  </body>

</html>
"]]

"The `<@=KEY>` values can be found with `lucid.publish.theme/load-settings`:"

(comment
  (lucid.publish/load-settings)
  ;; {:email "z@caudate.me",
  ;;  :date "06 October 2016",
  ;;  :copy ["assets"],
  ;;  :tracking-enabled "true",
  ;;  :site "lucid",
  ;;  :time "08 12",
  ;;  :manifest [... files ...],
  ;;  :icon "favicon",
  ;;  :defaults {:site "stark",
  ;;             :icon "favicon",
  ;;             :tracking-enabled "false",
  ;;             :template "article.html",
  ;;             :theme-base "theme-base-0b",
  ;;             :logo-white "img/logo-white.png",
  ;;             :article [...],
  ;;             :outline [...],
  ;;             :top-level [...]},
  ;;  :theme "stark",
  ;;  :author "Chris Zheng",
  ;;  :render {:article "render-article",
  ;;           :outline "render-outline",
  ;;           :top-level "render-top-level"},
  ;;  :tracking "UA-31320512-2",
  ;;  :resource "theme/stark",
  ;;  :engine "winterfell"}
  )

"And more values can be added to the `[:publish :template]` or `[:publish :template :defaults]` entry in `project.clj`. Please reference the [stark](https://github.com/zcaudate/lucidity/blob/master/resources/theme/stark) and [bolton](https://github.com/zcaudate/lucidity/blob/master/resources/theme/bolton) themes to see how to change a template to suit your needs."
