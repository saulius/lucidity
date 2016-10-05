(ns documentation.lucid-mind
  (:use hara.test)
  (:require [lucid.mind :refer :all]))

[[:chapter {:title "Introduction"}]]

"`lucid.mind` gives greater understanding of objects by providing a macro language on top of [hara.reflect](http://docs.caudate.me/hara/hara-reflect.html) such that object introspection becomes very easy when working with the jvm. This library was originally [iroh](https://github.com/zcaudate/hara/iroh) and then [vinyasa.reflection](https://github.com/zcaudate/vinyasa)." 

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:"

[[{:stencil true}]]
(comment
  [tahto/lucid.mind "{{PROJECT.version}}"])

"All functionality is in the `lucid.mind` namespace:"

(comment
  (use 'lucid.mind))

[[:section {:title "Motivation"}]]

"The words *everything is data* are taken literally when using `lucid.mind` to explore the world beyond clojure. Although functional programming have influenced the direction of Java as a whole, the object-orientated paradigm has a very deep hold on the language.

One OO *paradigm* - that of encapsulation - has been turned on it's head. In OO, it is a feature, in FP, it is a hinderence. `lucid.mind` bridges this gap. Taking inspiration from [clj-wallhack](https://github.com/arohner/clj-wallhack), here are some primary use cases for the library:

- To explore the members of classes as well as all instances within the repl
- To be able to test methods and functions that are usually not testable, or very hard to test:
  - Make hidden class members visible by providing access to private methods and fields
  - Make immutable class members flexible for initialisation of state
- Extract out class members into documented and executable functions (including multi-argument functions)
- Better understand jvm security and how to dodge it if needed
- Better understand the java type system as well as clojure's own interface definitions
- To make working with java fun and informative
"

[[:chapter {:title "API" :link "lucid.mind"}]]

[[:api {:title "" :namespace "lucid.mind"}]]

[[:chapter {:title "Fields"}]]

[[:section {:title "delegate" :tag "delegate"}]]

"`.&` does what bean does but it actually allows field access to the underlying object through reflection. This way, one can set and get values from any object, regardless of permission model (private, protected, etc...)."

"First we create a string:"

(fact
  (def a "hello")
  a => "hello")

"Then we create the delegate for the string"

(def >a (.& a))

>a
;;=> <java.lang.String@99162322 {:hash 99162322, :hash32 0, :value #<char[] [C@202cf33f>}>

"We can dereference the delegate to get the underlying fields of the object"

@>a          ;;=> {:hash 99162322, :hash32 0, :value #<char[] [C@202cf33f>}

(keys >a)    ;;=> (:value :hash :hash32)

"The delegate behaves exactly like a map:"

(>a :hash)   ;;=> 99162322

(:hash32 >a) ;;=> 0

"The value of the string `a` can also be changed through it's delegate `>a`, even though `value` is a private, final field:"

(fact
  (>a :value (char-array "world")) ;;=> "world"

  a => "world")

[[:section {:title "class threading" :tag "threading"}]]

"`.>` is a convenience macro for accessing the innards of an object. It is akin to the threading `->` macro except that now private fields can also be accessed:"

(fact
  (.> "hello" (:value) seq)
  => [\h \e \l \l \o])

"changing the value of the string"

(fact
  (.> "hello" (:value "world") (:value) seq)
  => [\w \o \r \l \d])

[[:chapter {:title "Classes"}]]

[[:section {:title "class info" :tag "info"}]]

"`.%` shows the infomation about a particular class or class instance:"

(fact
  (.% String)
  => (contains {:name "java.lang.String",
                :hash number?
                :modifiers #{:instance :public :final :class}}))

"This can also be used on a class instance itself:"

(fact
  (.% "abc")
  => {:name "java.lang.String",
      :hash 206835546,
      :modifiers #{:instance :public :final :class}})

[[:section {:title "class hierarchy" :tag "hierarchy"}]]

"`.%>` shows the class hierarchy for a particular class or class instance:"

(fact
  (.%> 1)
  => [java.lang.Long
      [java.lang.Number #{java.lang.Comparable}]
      [java.lang.Object #{java.io.Serializable}]])

"To read this, the first entry `java.lang.Long` is the actual type of the object. It extends `java.lang.Number` and implements the `java.lang.Comparable` interface. `java.lang.Number` extends `java.lang.Object` and implements the `java.io.Serializable` interface. So we can see the entire inheritance structure for the input `1`."

"A additional example is for a string:"

(fact
  (.%> "hello")
  => [java.lang.String
      [java.lang.Object #{java.lang.CharSequence
                          java.io.Serializable
                          java.lang.Comparable}]])

"And for a clojure map:"

(fact
  (.%> {})
  => [clojure.lang.PersistentArrayMap
      [clojure.lang.APersistentMap #{clojure.lang.IKVReduce
                                     clojure.lang.IObj
                                     clojure.lang.IEditableCollection
                                     clojure.lang.IMapIterable}]
      [clojure.lang.AFn #{java.util.Map
                          java.io.Serializable
                          clojure.lang.MapEquivalence
                          clojure.lang.IHashEq
                          java.lang.Iterable
                          clojure.lang.IPersistentMap}]
      [java.lang.Object #{clojure.lang.IFn}]])

"As you can see, a map is not that simple."

[[:chapter {:title "Queries"}]]

[[:section {:title "class query" :tag "class"}]]

"`.?` is used to list all methods belonging to a single class:"

(.? 1)
;; (#[BYTES :: <java.lang.Long> | int]
;;  #[MAX_VALUE :: <java.lang.Long> | long]
;;  #[MIN_VALUE :: <java.lang.Long> | long]
;;  ...
;;  ...
;;  #[toUnsignedString :: (long) -> java.lang.String]
;;  #[toUnsignedString0 :: (long, int) -> java.lang.String]
;;  #[value :: (java.lang.Long) | long]
;;  #[valueOf :: (java.lang.String, int) -> java.lang.Long]
;;  #[valueOf :: (long) -> java.lang.Long]
;;  #[valueOf :: (java.lang.String) -> java.lang.Long])

"It's not that useful to just return a whole bunch of elements and so the query is customizable through are many filters that can be used."

[[:section {:title "filter on name"}]]

"The function name can be matched using either a string for exact match or a regex pattern for fuzzy match. To find all elements called `valueOf`, a string is used as input:"

(.? 1 "valueOf")
;; (#[valueOf :: (java.lang.String, int) -> java.lang.Long]
;;  #[valueOf :: (long) -> java.lang.Long]
;;  #[valueOf :: (java.lang.String) -> java.lang.Long])

"To find all elements starting with the letter `c` a regex can be constructed:"

(.? 1 #"^c")
;; (#[compare :: (long, long) -> int]
;;  #[compareTo :: (java.lang.Long, java.lang.Object) -> int]
;;  #[compareTo :: (java.lang.Long, java.lang.Long) -> int]
;;  #[compareUnsigned :: (long, long) -> int])

[[:section {:title "elements"}]]

"The query returns a list of elements. Each single element can be further explored. A query looking for methods named `parselong` yields two methods:"

(.? 1 "parseLong")
;; (#[parseLong :: (java.lang.String, int) -> long]
;;  #[parseLong :: (java.lang.String) -> long])

"The element can be captured to a variable."

(def parse-long (second (.? 1 "parseLong")))

"Printing out the variable sees that it is a method that takes a string as input as returns a long."

(fact
  (str parse-long)
  => "#[parseLong :: (java.lang.String) -> long]")

"The variable can used like any other clojure method:"

(fact
  (parse-long "202")
  => 202)

"Introspection of the element reveals that it indeed extends IFn and ILookup:"

(fact
  (.%> parse-long)
  => [hara.reflect.types.element.Element
      [java.lang.Object #{clojure.lang.IType
                          clojure.lang.IFn
                          clojure.lang.ILookup}]])

"`parse-long` contains a lot more information that allows for it to behave like a typed function. The keyword `:all` accesses the data for the element:"

(fact
  (:all parse-long)
  => (contains {:origins [java.lang.Long]
                :hash number?
                :delegate java.lang.reflect.Method
                :name "parseLong",
                :static true,
                :params [java.lang.String],
                :type Long/TYPE
                :modifiers #{:method :public :static},
                :container java.lang.Long,
                :tag :method}))

"Individual attributes are accessible through keyword lookup:"

(fact
  (:name parse-long)
  => "parseLong")

[[:section {:title "selection"}]]

"Queries can be made with specific attribues returned, instead of the entire element by specifying options:"

(fact
  (.? 1 #"^parse" :name)
  => ["parseLong" "parseUnsignedLong"]

  (.? 1 #"^parse" :name :params)
  => [{:name "parseLong", :params [java.lang.String Integer/TYPE]}
      {:name "parseLong", :params [java.lang.String]}
      {:name "parseUnsignedLong", :params [java.lang.String Integer/TYPE]}
      {:name "parseUnsignedLong", :params [java.lang.String]}])

"The most useful keyword is `:name` as it provides a succinct way of listing the contents of a class:"

(fact
  (.? 1 :name)
  => (contains
      ["BYTES" "MAX_VALUE" "MIN_VALUE" "SIZE"
       "TYPE" "bitCount" "byteValue" "compare"
       "compareTo" "compareUnsigned" "decode"
       "divideUnsigned" "doubleValue" "equals"
       "floatValue" "formatUnsignedLong" "getChars"
       "getLong" "hashCode" "highestOneBit"
       "intValue" "longValue" "lowestOneBit"
       "max" "min" "new" "numberOfLeadingZeros"
       "numberOfTrailingZeros" "parseLong"
       "parseUnsignedLong" "remainderUnsigned"
       "reverse" "reverseBytes" "rotateLeft"
       "rotateRight" "serialVersionUID" "shortValue"
       "signum" "stringSize" "sum" "toBinaryString"
       "toHexString" "toOctalString" "toString"
       "toUnsignedBigInteger" "toUnsignedString"
       "toUnsignedString0" "value" "valueOf"]))

[[:section {:title "filter on modifiers"}]]

"There are a list of modifiers that can be used for filtering:"

(def flags
  
  [;; Element Type
   :field
   :method

   ;; Element Encapsulation
   :plain          
   :public          
   :private         
   :protected       

   ;; Element Attributes
   :static          
   :final           
   :synchronized    
   :native          
   :abstract])

"For example, listing all the private fields of a string:"

(comment
  (.? String :name :private :field)
  => ("hash" "serialPersistentFields" "serialVersionUID" "value"))

"All static methods of String:"

(comment
  (.? String :name :method :static)
  => ("checkBounds" "copyValueOf" "format" "indexOf" "join" "lastIndexOf" "valueOf"))

"All the private non-static field names in String:"

(comment
  (.? String :name :private :field :instance)
  => ["hash" "value"])

[[:section {:title "multiselect"}]]

"Going back to `parseLong`, it can be seen that there are two methods listed"

(.? 1 "parseLong")
;; (#[parseLong :: (java.lang.String, int) -> long]
;;  #[parseLong :: (java.lang.String) -> long])

"In order to get all of them under the same name, use `:#`"

(.? 1 "parseLong" :#)
;; #[parseLong :: ([java.lang.String int]), ([java.lang.String])]

"The type signature can take two types of inputs, as seen below:"

(def parse-long-multi (.? 1 "parseLong" :#))

(fact
  (parse-long-multi "10")
  => 10
  
  (parse-long-multi "10" 10)
  => 10
  
  (parse-long-multi "10" 2)
  => 2)

"This is very useful for including a bunch of methods"

[[:section {:title "filter on signatures"}]]

"The signature of methods can also be explored. A list of the name and params of methods starting with `s` that have 3 inputs are shown below:"

(comment
  (.? String :name :params #"^s" 3)
  => ({:name "split",
       :params [java.lang.String java.lang.String int]}
      {:name "startsWith",
       :params [java.lang.String java.lang.String int]}
      {:name "subSequence",
       :params [java.lang.String int int]}
      {:name "substring",
       :params [java.lang.String int int]}))

"The return type can be filtered by specifying a class. A list of methods that return a `java.lang.String` object are shown below:"

(comment
  (.? String :name String)
  => ("concat" "copyValueOf" "format" "intern"
      "join" "new" "replace" "replaceAll"
      "replaceFirst" "substring" "toLowerCase"
      "toString" "toUpperCase" "trim" "valueOf"))

"Input types can also be filtered by specifing an array with the type signature. A list of methods that take a `java.lang.String` as input are shown below:"

(comment
  (.? String :name :method [String])
  => ("getBytes" "hashCode" "intern" "isEmpty"
      "length" "toCharArray" "toLowerCase"
      "toString" "toUpperCase" "trim"))

"Match for any public method that contains an `int` in the input signature:"

(comment
  (.? String :name :public :method [:any int])
  => ("charAt" "codePointAt" "codePointBefore"
      "codePointCount" "copyValueOf" "getBytes"
      "getChars" "indexOf" "lastIndexOf"
      "offsetByCodePoints" "regionMatches"
      "split" "startsWith" "subSequence"
      "substring" "valueOf"))


[[:section {:title "instance query" :tag "instance"}]]

"`.*` is very similar to `.?` in that it also inspects a variable and they have the same listing and filtering mechanisms. However,  `.?` holds the java view of the Class declaration, staying true to the class and its members. `.*` holds the runtime view of Objects and what methods could be applied to that instance. `.*` will also look up the inheritance tree to fill in additional functionality. "

"Below shows the difference when asking for members of String beginning with `c`.:"

(comment
  (.? String  #"^c" :name)
  => ("charAt" "checkBounds" "codePointAt" "codePointBefore"
      "codePointCount" "compareTo" "compareToIgnoreCase"
      "concat" "contains" "contentEquals" "copyValueOf"))


(comment
  (.* String #"^c" :name)
  => ("cachedConstructor" "cannotCastMsg" "casAnnotationType"
      "cast" "checkBounds" "checkMemberAccess" "checkPackageAccess"
      "classRedefinedCount" "classValueMap" "clone"
      "copyValueOf" "createAnnotationData"))

"`.?` lists is what we expect when we read the documentation on `java.lang.String`. `.*` lists all static methods and fields as well as Class methods of `String`, whilst for instances of `String`, it will list all the instance methods from the entire class hierachy."

"A further comparison can be seen when listing the public methods:"

(comment
  (.? {} :name :method :public)
  => ("asTransient" "assoc" "assocEx" "capacity" "containsKey"
      "count" "create" "createAsIfByAssoc" "createWithCheck"
      "empty" "entryAt" "iterator" "keyIterator" "kvreduce"
      "meta" "seq" "valAt" "valIterator" "withMeta" "without"))

(comment
  (.* {} :name :method :public)
  => ("applyTo" "asTransient" "assoc" "assocEx" "call" "capacity"
      "clear" "cons" "containsKey" "containsValue" "count" "empty"
      "entryAt" "entrySet" "equals" "equiv" "get" "getClass" "hashCode"
      "hasheq" "invoke" "isEmpty" "iterator" "keyIterator" "keySet"
      "kvreduce" "meta" "notify" "notifyAll" "put" "putAll" "remove"
      "run" "seq" "size" "throwArity" "toString" "valAt" "valIterator"
      "values" "wait" "withMeta" "without"))

"`.?` lists only the class declarations whereas `.*` lists all methods available to the instance, including methods from further up the inheritance tree. It can be said that the difference between the two is that `.?` takes the class view, whereas `.*` takes the runtime view."

[[:chapter {:title "Import"}]]

[[:section {:title "import as var" :tag "import-var"}]]

"We can extract methods from a Class or interface with `.>var`:"

(comment
  (.>var hash-without [clojure.lang.IPersistentMap without]
         hash-assoc [clojure.lang.IPersistentMap assoc])
  => [#'documentation.lucid-mind/hash-without
      #'documentation.lucid-mind/hash-assoc]
  
  (clojure.repl/doc hash-without)
  ;; documentation.lucid-mind/hash-without
  ;; [[clojure.lang.PersistentArrayMap java.lang.Object]]
  ;;
  ;; member: clojure.lang.PersistentArrayMap/without
  ;; type: clojure.lang.IPersistentMap
  ;; modifiers: instance, method, public

  (hash-without {:a 1 :b 2} :a)
  => {:b 2}
  
  (clojure.repl/doc hash-assoc)
  ;; documentation.lucid-mind/hash-assoc
  ;; [[clojure.lang.IPersistentMap java.lang.Object java.lang.Object]]
  ;;
  ;; member: clojure.lang.IPersistentMap/assoc
  ;; type: clojure.lang.IPersistentMap
  ;; modifiers: instance, method, public, abstract

  (hash-assoc {:a 1 :b 2} :c 3)
  => {:a 1, :b 2, :c 3})

[[:section {:title "import as namespace" :tag "import-ns"}]]

"We can extract an entire class into a namespace. These are modifiable by selectors from `.?`. This is very useful for exploring a class by dumping out the contents into a test namespace:"

(comment
  (.>ns test.string String :public #"^c" 2)
  => (#'test.string/charAt
      #'test.string/codePointAt
      #'test.string/codePointBefore
      #'test.string/compareTo
      #'test.string/compareToIgnoreCase
      #'test.string/concat
      #'test.string/contains
      #'test.string/contentEquals)
  
  (str test.string/concat)
  => "#[concat :: (java.lang.String, java.lang.String) -> java.lang.String]"
  
  (test.string/concat "hello " "world")
  => "hello world")
