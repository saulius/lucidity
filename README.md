# vinyasa

[Give your clojure workflow more flow](http://z.caudate.me/give-your-clojure-workflow-more-flow/)

[![Build Status](https://travis-ci.org/zcaudate/vinyasa.svg?branch=master)](https://travis-ci.org/zcaudate/vinyasa)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents** [[doctoc](https://github.com/thlorenz/doctoc)]

- [vinyasa](#vinyasa)
	- [Whats New](#whats-new)
	- [Installation](#installation)
	- [Quickstart:](#quickstart)
		- [pull](#pull)
		- [lein](#lein)
		- [reimport](#reimport)
		- [inject](#inject)
		- [reflection](#reflection)
		- [classloader](#classloader)
		- [maven](#maven)
	- [License](#license)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Whats New

#### 0.3.4
- upgraded to use [hara.reflect](https://github.com/zcaudate/hara) 2.1.11

#### 0.3.2
- merged functionality [korra](https://github.com/zcaudate/korra) into `vinyasa.maven` and `vinyasa.classloader`

#### 0.3.0
- merged functionality [iroh](https://github.com/zcaudate/iroh) into `vinyasa.reflection`

#### 0.2.2
- breaking changes to `vinyasa.inject/inject`, see [example](#inject)
- a new helper macro `vinyasa.inject/in` for prettier imports, see [example](#installation)

#### 0.2.0

- vinyasa has now been [repackaged](https://github.com/zcaudate/lein-repack)


#### 0.1.9
Changed `vinyasa.lein` according to [comments](http://z.caudate.me/clojure-dynamic-languages-creativity-and-simplicity/) on blog.

WARNING: There are [issues](https://github.com/zcaudate/vinyasa/issues/3) with adding leiningen as a dependency. It should be disabled if it causes problems.

#### 0.1.8
Breaking changes to `reimport`. Now reimport is used like this:

```clojure
(reimport :all)  ;; compile import all symbols into namespace

(reimport 'com.example.Util
          '[net.example Hello World]
          false) ;; do not import symbols
```
## Installation

Add `vinyasa` to your `profiles.clj` (located in `~/.lein/profiles.clj`) as well as your version of leiningen. Please note the issue with `vinyasa.lein` with light table as well as other libraries. You may need to disable `vinyasa.lein` and `leiningen` if there are problems.

`inject` has been quite popular due this [article](http://dev.solita.fi/2014/03/18/pimp-my-repl.html). It's main use is to create extra symbols in a particular namespace, namely `clojure.core`. Customisation of your namespace can be done by injecting functions in seperate namespaces into either `clojure.core` or another namespace. This is typically done through the `:injections` value in your `~/.lein/profiles.clj` file.

Previously, injecting functions into `clojure.core` with a prefix was the way to avoid clashing names. This is still a good option but it may not be the best option going forward. One [issue](https://github.com/zcaudate/vinyasa/issues/9) noted that `(in-ns <namespace>)` call will not automatically call `(refer-clojure)` and so will not import methods from `clojure.core`. 

As such `inject` has undergone quite a bit of refinement since version `0.2.2`. 
 
- Firstly, it is suggested that a short namespace be used instead of adding a prefix. For example, instead of typing `>pprint`, we type `>/pprint` or by default, `./pprint` (the suggested default namespace is now "`.`").
    
- Secondly, `vinyasa.inject/in` macro is used so that there is less quoting and is semantically similar to the `ns` macro. 
  
Here is an example of a typical `profiles.clj` configuration:

```clojure
{:user 
  {:plugins [...]        
   :dependencies [[spyscope "0.1.4"]
                  [org.clojure/tools.namespace "0.2.4"]
                  [leiningen #=(leiningen.core.main/leiningen-version)]
                  [io.aviso/pretty "0.1.8"]
                  [im.chit/vinyasa "0.3.4"]]
   :injections 
   [(require 'spyscope.core)
    (require '[vinyasa.inject :as inject])
    (require 'io.aviso.repl)
    (inject/in ;; the default injected namespace is `.` 

               ;; note that `:refer, :all and :exclude can be used
               [vinyasa.inject :refer [inject [in inject-in]]]  
               [vinyasa.lein :exclude [*project*]]  

               ;; imports all functions in vinyasa.pull
               [vinyasa.pull :all]      

               ;; same as [cemerick.pomegranate 
               ;;           :refer [add-classpath get-classpath resources]]
               [cemerick.pomegranate add-classpath get-classpath resources] 
               
               ;; inject into clojure.core 
               clojure.core
               [vinyasa.reflection .> .? .* .% .%> .& .>ns .>var]
               
               ;; inject into clojure.core with prefix
               clojure.core >
               [clojure.pprint pprint]
               [clojure.java.shell sh])]}}
```

The following vars will now be created under the `.` namespace:

```clojure
user=> ./
./>ns             ./>var            ./add-classpath   ./apropos         ./dir             ./doc
./find-doc        ./get-classpath   ./inject          ./inject-in       ./pprint          ./pst
./pull            ./refresh         ./resources       ./root-cause      ./sh              ./source
```

Reflection macros: `.>` `.?` `.*` `.%` `.%>` `.&` `.>ns` `.>var` will be created in `clojure.core`

```clojure
user=> (.? "" :name)
("CASE_INSENSITIVE_ORDER" "HASHING_SEED" "charAt" "checkBounds" "codePointAt" "codePointBefore" "codePointCount" "compareTo" "compareToIgnoreCase" "concat" "contains" "contentEquals" "copyValueOf" "endsWith" "equals" "equalsIgnoreCase" "format" "getBytes" "getChars" "hash" "hash32" "hashCode" "indexOf" "indexOfSupplementary" "intern" "isEmpty" "lastIndexOf" "lastIndexOfSupplementary" "length" "matches" "new" "offsetByCodePoints" "regionMatches" "replace" "replaceAll" "replaceFirst" "serialPersistentFields" "serialVersionUID" "split" "startsWith" "subSequence" "substring" "toCharArray" "toLowerCase" "toString" "toUpperCase" "trim" "value" "valueOf")
```

Prefixed vars `>pprint` and `>sh` will be created in `clojure.core`

```clojure
user=> >
>    >=   >pprint   >sh
```

*NOTE* Its very important that `leiningen` is in your dependencies for `vinyasa.lein` and `vinyasa.import` as `lein` and `reimport` have dependencies on leiningen functions

## Quickstart:

Once `profiles.clj` is installed, run `lein repl`.

```clojure
> (./lein)    ;; => entry point to leiningen
> (./reimport) ;; => dynamically reloads *.java files
> (./pull 'hiccup) ;; => pull repositories from clojars
> (./inject 'clojure.core '[[hiccup.core html]]) ;; => injects new methods into clojure.core
> (html [:p "Hello World"]) ;; => injected method
;;=> "<p>hello world</p>"
```

### pull

How many times have you forgotten a library dependency for `project.clj` and then had to restart your nrepl? `pull` is a convienient wrapper around the `pomegranate` library:

```clojure
> (require 'hiccup.core)
;; => java.io.FileNotFoundException: Could not locate hiccup/core__init.class or hiccup/core.clj on classpath:

> (require 'hiccup.core)
> (pull 'hiccup)
;; => {[org.clojure/clojure "1.2.1"] nil,
;;     [hiccup "1.0.4"] #{[org.clojure/clojure "1.2.1"]}}

> (use 'hiccup.core)
> (html [:p "hello World"])
;; => "<p>hello World</p>"

> (pull 'hiccup "1.0.1")
;; => {[org.clojure/clojure "1.2.1"] nil,
;;     [hiccup "1.0.1"] #{[org.clojure/clojure "1.2.1"]}}
```
### lein

Don't you wish that you had the power of leiningen within the repl itself? `lein` is that entry point. You don't have to open up another terminal window anymore, You can now run your commands in the repl!

```clojure
> (lein)
;; Leiningen is a tool for working with Clojure projects.
;;
;; Several tasks are available:
;; check               Check syntax and warn on reflection.
;; classpath           Write the classpath of the current project to output-file.
;; clean               Remove all files from paths in project's clean-targets.
;; cljsbuild           Compile ClojureScript source into a JavaScript file.
;;
;;  .....
;;  .....

> (lein install)     ;; Install to local maven repo

> (lein uberjar)     ;; Create a jar-file

> (lein push)        ;; Deploy on clojars (I am using lein-clojars plugin)

> (lein javac)       ;; Compile java classes (use vinyasa.reimport instead)

```

### reimport

Don't you wish that you could make some changes to your java files and have them instantly loaded into your repl without restarting? Well now you can!

For example, in project.clj, you have specified your `:java-source-paths`

```clojure
(defproject .....
   :source-paths ["src/clojure"]
   :java-source-paths ["src/java"]
   :java-test-paths ["test/java"]    ;; *.java files that are not included in package
   ....)
```

and you have a file `src/java/testing/Dog.java`

```java
package testing;
public class Dog{
  public int legs = 3;
  public Dog(){};
}
```

You can load it into your library dynamically using `reimport`

```clojure
(reimport 'testing.Dog)
;;=> 'testing.Dog' imported from <project>/target/reload/testing/Dog.class

(.legs (Dog.))
;; => 3
```

You can then change legs in `testing.Dog` from `3` to `4`, save and go back to your repl:

```clojure
(reimport '[testing Dog]) ;; supports multiple classes
;;=> 'testing.Dog' imported from <project>/target/reload/testing/Dog.class

(.legs (Dog.))
;; => 4
```

If you have more files, ie. copy your Dog.java file to Cat.java and do a global replace:

```clojure
(reimport) ;; will load all classes into your namespace
;;=> 'testing.Dog' imported from <project>/target/reload/testing/Dog.class
;;   'testing.Cat' imported from <project>/target/reload/testing/Cat.class

(.legs (Cat.))
;; => 4
```

Now the pain associated with mixed clojure/java development is gone!

### inject

I find that when I am debugging, there are additional functionality that is needed which is not included in clojure.core. The most commonly used function is `pprint` and it is much better if the function came with me when I was debugging.

The best place to put all of these functions in in the `clojure.core` namespace
`inject` is used to add additional functionality to namespaces so that the functions are there right when I need them. Inject also works with macros and functions (unlike `intern` which only works with functions):

```clojure
> (inject '[clojure.core [clojure.repl dir]])
;; => will intern #'clojure.repl/dir to #'clojure.core/dir

> (clojure.core/dir clojure.core)
;; *
;; *'
;; *1
;; *2
;; *3
;; *agent*
;; *allow-unresolved-vars*
;; *assert*
;;
;; ...
;; ...
```

`inject` can also work with multiple entries:

```clojure
> (inject '[clojure.core [clojure.repl doc source]])
;; => will create the var #'clojure.core/doc and #'clojure.core/source
```

`inject` can also take a prefix:

```clojure
> (inject '[clojure.core >> [clojure.repl doc source]])
;; => will create the var #'clojure.core/>>doc and #'clojure.core/>>source
```

`inject` can use vector bindings to directly specify the name

```clojure
> (inject '[clojure.core >> [clojure.repl doc [source source]])
;; => will create the var #'clojure.core/>>doc and #'clojure.core/source
```

### reflection

Although private and protected keywords have their uses in java, they are complete hinderences when I am trying to do something to the code base that the previous author had not intended for the user to do - one of them being to understand what is going on underneath. If the previous author had taken shortcuts in design, those private keywords turn one of those over-protective parents that get in the way of the growth of their children. Taking inspiration from clj-wallhack, here are some primary use cases for the library:   

- To explore the members of classes as well as all instances within the repl
- To be able to test methods and functions that are usually not testable, or very hard to test:
  - Make hidden class members visible by providing access to private methods and fields 
  - Make immutable class members flexible by providing ability to change final members (So that initial states can be set up easily)
- Extract out class members into documented and executable functions (including multi-argument functions)
- Better understand jvm security and how to dodge it if needed
- Better understand the java type system as well as clojure's own interface definitions
- To make working with java fun again

Main functionality is accessed through:

```clojure
(use 'vinyasa.reflection)
```

The api consists of the following macros:

```clojure
  .& - for transparency into objects
  .% - for showing class properties
  .%> - for showing type hierarchy
  .? - for showing class elements
  .* - for showing instance elements
  .> - threading macro for reflective invocation of objects
  >ns - for importing object elements into a namespace
  >var - for importing elements into current namespace
```

#### `.&` - Transparent Bean 

`.&` does what bean does but it actually allows transparent field access to the underlying object. This way, one can set and get values from any object, regardless of permission model (private, protected, etc...):

```clojure
(def a "hello")
a  ;;=> "hello" 

(def >a (.& a))
>a ;;=> <java.lang.String@99162322 {:hash 99162322, :hash32 0, :value #<char[] [C@202cf33f>}>

@>a          ;;=> {:hash 99162322, :hash32 0, :value #<char[] [C@202cf33f>}
(keys >a)    ;;=> (:value :hash :hash32)
(>a :hash)   ;;=> 99162322
(:hash32 >a) ;;=> 0  
(>a :value (char-array "world")) ;;=> "world"

a ;;=> "world" (But I thought strings where immutable!)
```

#### `.%` - Type Info

`.%` shows the infomation about a particular class or class instance:

(.% "abc")  ;; or (.% String)
=> (contains {:name "java.lang.String"
              :tag :class
              :hash anything
              :container nil
              :modifiers #{:instance :class :public :final}
              :static false
              :delegate java.lang.String})

#### `.%>` - Type Hierarchy

`.%>` shows the class hierarchy for a particular class or class instance:

```clojure
(.%> 1)
;;=> [java.lang.Long
;;    [java.lang.Number #{java.lang.Comparable}]
;;    [java.lang.Object #{java.io.Serializable}]]

(.%> "hello")
;;=> [java.lang.String
;;    [java.lang.Object #{java.lang.CharSequence
;;                        java.io.Serializable
;;                        java.lang.Comparable}]]

(.%> {})
;;=> [clojure.lang.PersistentArrayMap
;;    [clojure.lang.APersistentMap #{clojure.lang.IObj
;;                                   clojure.lang.IEditableCollection}]
;;    [clojure.lang.AFn #{clojure.lang.MapEquivalence
;;                        clojure.lang.IHashEq
;;                        java.io.Serializable
;;                        clojure.lang.IPersistentMap
;;                        java.util.Map
;;                        java.lang.Iterable}]
;;    [java.lang.Object #{clojure.lang.IFn}]]
```

#### `.?` and `.*` - Exploration

`.?` and `.*` have the same listing and filtering mechanisms but they do things a little differently. `.?` holds the java view of the Class declaration, staying true to the class and its members. `.*` holds the runtime view of Objects and what methods could be applied to that instance. `.*` will also look up the inheritance tree to fill in additional functionality. 

Below shows three examples. All the method asks for members of String beginning with `c`.:

```clojure
(.? String  #"^c" :name)
;;=> ["charAt" "checkBounds" "codePointAt" "codePointBefore"
;;    "codePointCount" "compareTo" "compareToIgnoreCase"
;;    "concat" "contains" "contentEquals" "copyValueOf"]

(.* String #"^c" :name)
;;=> ["cachedConstructor" "cannotCastMsg" "cast" "checkBounds" 
;;    "checkMemberAccess" "classRedefinedCount" "classValueMap" 
;;    "clearCachesOnClassRedefinition" "clone" "copyValueOf"]

(.* (String.) #"^c" :name)
;;=> ["charAt" "clone" "codePointAt" "codePointBefore" 
;;    "codePointCount" "compareTo" "compareToIgnoreCase" 
;;    "concat" "contains" "contentEquals"]

`.?` lists is what we expect. `.*` lists all static methods and fields as well as Class methods of `String`, whilst for instances of `String`, it will list all the instance methods from the entire class hierachy.
```

There are many filters that can be used with `.?` ande `.*`:

  - regexes and strings for filtering of element names
  - symbols and classes for filtering of return type
  - vectors for filtering of input types
  - longs for filtering of input argment count
  - keywords for filtering of element modifiers
  - keywords for customization of return types

Below are examples of results All the private element names in String beginning with `c`:

```clojure
(.? String  #"^c" :name :private)
;;=> ["checkBounds"]
```

All the private field names in String:

```clojure
(.? String :name :private :field)
;;=> ["HASHING_SEED" "hash" "hash32" "serialPersistentFields"
;;    "serialVersionUID" "value"]
```

All the private static field names in String:

```clojure
(.? String :name :private :field :static)
;;=> ["HASHING_SEED" "serialPersistentFields" "serialVersionUID"]
```

All the private non-static field names in String:

```clojure
(.? String :name :private :field :instance)
;;=> ["hash" "hash32" "value"]
```

##### Example
In the following example, We can assign functions to var `char-at`:

```clojure
(def char-at (first (.? String "charAt" :#)))

    or

(def char-at (.? String "charAt" :#))
```

`char-at` is an method element. It can be turned into a string:

```clojure
(str char-at)
;;=> "#[charAt :: (java.lang.String, int) -> char]"
```

From the string, it hints that `char-at` is invokable. The element takes in a `String` and an `int`, returning a `char`. It can be used like any other clojure function.

```clojure
(char-at "hello" 0) => \h

(mapv #(char-at "hello" %) (range 5))  => [\h \e \l \l \o]
```

Data for char-at is accessed using keyword lookups:

```clojure
(:params char-at) => [java.lang.String Integer/TYPE]

(:modifiers char-at) => #{:instance :method :public}

(:type char-at)  => Character/TYPE

(:all char-at)
=> (just {:tag :method
          :name "charAt"
          :modifiers #{:instance :method :public},
          :origins [CharSequence String]
          :hash number?
          :container String
          :static false
          :params [String Integer/TYPE]
          :type Character/TYPE
          :delegate fn?})
```


Private class members and fields can be exposed as easily as public ones. First, a list of private methods defined in Integers are listed:

```clojure
(.? Integer :private :method :name)
=> ["toUnsignedString"]
```

Since there is only toUnsignedString, it will be extracted:


```clojure
(def unsigned-str (.? Integer "toUnsignedString" :#))
```

As can be seen by its modifiers, unsigned-str is private static method:


```clojure
(:modifiers unsigned-str)
;;=> #{:method :private :static}
```

The string representation shows that it takes two ints and returns a String:


```clojure
(str unsigned-str)
;;=> "#[toUnsignedString :: (int, int) -> java.lang.String]"
```

The element can now be used, just like a normal function:

```clojure
(unsigned-str 10 1)
;;=> "1010"

(mapv #(unsigned-str 32 (inc %)) (range 6))
;;=> ["100000" "200" "40" "20" "10" "w"]
```

#### `.>` - Threading
`.>` is a convenience macro for accessing the innards of an object. It is akin to the threading `->` macro except that now private fields can also be accessed:

```clojure
(def a "hello")
(.> a :value) ;=> #<char[] [C@753f827a>
(.> a (:value (char-array "world")))
a ;;=> "world"
```


#### `>var` - Import as Var

We can extract methods from a Class or interface with `>var`

```clojure
(>var hash-without [clojure.lang.IPersistentMap without]
      hash-assoc [clojure.lang.IPersistentMap assoc])

(clojure.repl/doc hash-without)
;;=> -------------------------
;;   midje-doc.iroh-walkthrough/hash-without
;;   ([clojure.lang.PersistentArrayMap java.lang.Object])
;;   ------------------
;;
;;   member: clojure.lang.PersistentArrayMap/without
;;   type: clojure.lang.IPersistentMap
;;   modifiers: instance, method, public

(str hash-without)
;; => "#[without :: (clojure.lang.PersistentArrayMap, java.lang.Object) -> clojure.lang.IPersistentMap]"

(hash-without {:a 1 :b 2} :a)
;; => {:b 2}

(str hash-assoc)
=> "#[assoc :: (clojure.lang.IPersistentMap, java.lang.Object, java.lang.Object) -> clojure.lang.IPersistentMap]"

(hash-assoc {:a 1 :b 2} :c 3)
;; => {:a 1 :b 2 :c 3}
```

#### `>ns` - Import as Namespace
We can extract an entire class into a namespace. These are modifiable by selectors, explained later:

```clojure
(>ns test.string String :private)
;; => [#'test.string/HASHING_SEED #'test.string/checkBounds
;;     #'test.string/hash #'test.string/hash32
;;     #'test.string/indexOfSupplementary
;;     #'test.string/lastIndexOfSupplementary
;;     #'test.string/serialPersistentFields #'test.string/serialVersionUID
;;     #'test.string/value]

(seq (test.string/value "hello"))
;;=> (\h \e \l \l \o)
```

### classloader

`vinyasa.classloader` provides a straight-forward method of loading a class from a file on the filesystem.

(use 'hara.classloader)

(load-class ["path/to/java_file.class"])
(load-class ["path/to/package.jar" "path/within/package.class"])

### maven

`vinyasa.maven` is a library for introspection of maven packages. The library provides mappings between different representations of the same jvm concept. 

- maven coordinate and the jar file 
- a 'resource' and its related jar and jar entry under a given context
    - the resource can be:
        - a symbol representing a clojure namespace
        - a path to a resource
        - a java class
    - the context can be:
        - the jvm classloader classpath
        - a single jar
        - a list of jars
        - a maven coordinate
        - a list of maven coordinates
        - the entire maven local-repo.
		
#### representational mapping

There is a reversible mapping between the maven jar file and the coordinate. We use `maven-file` and `maven-coordinate` to transition from one to the other:

```clojure
(use 'vinyasa.maven.jar)

(maven-file '[org.clojure/clojure "1.6.0"])
;; => "/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar"


(use 'vinyasa.maven)

(maven-coordinate "/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar")
;; => [org.clojure/clojure "1.6.0"]
```

There is also a mapping between a clojure namespace, a java class and the their location in a jar.

```clojure
(jar-entry "/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar"
             'clojure.core)
;; => #<JarFileEntry clojure/core.clj>
```

#### resolve-jar

The main work-horse is for korra is `resolve-jar`. It resolves a `resource` and a `context`. The default context is the current jvm classpath:

```clojure
(resolve-jar 'clojure.core)
;; => ["/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar" "clojure/core.clj"]
```

It will resolve classes:

```clojure
(resolve-jar java.lang.Object)
;;=> ["/Library/Java/JavaVirtualMachines/jdk1.7.0_60.jdk/Contents/Home/jre/lib/rt.jar" "java/lang/Object.class"]
```

It will also resolve strings:

```clojure
(resolve-jar "clojure/core.clj")
;;=> ["/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar" "clojure/core.clj"]
```

Symbols with the last section capitalized will default to java classes instead of clojure files:

```clojure
(resolve-jar 'clojure.lang.IProxy)
;;=> ["/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar" "clojure/lang/IProxy.class"]
```

It will return nil if the resource cannot be found:

```clojure
(resolve-jar 'does.not.exist)
;; => nil
```

#### search contexts

Apart from searching via the current jvm classpath, other search contexts can be set, the most simple being a string representation of the jar path:

```clojure
(resolve-jar 'clojure.core "/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar")
;; => ["/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar" "clojure/core.clj"]
```

If the entry cannot be found, nil will be returned:

```clojure
(resolve-jar 'clojure.core "/Users/zhengc/.m2/repository/dynapath/dynapath/0.2.0/dynapath-0.2.0.jar")
;; => nil
```

In addition to the jar, one can use as the contexte a vector of jar-files:

```clojure
(resolve-jar 'clojure.core ["/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar"])
;; => ["/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar" "clojure/core.clj"]
```

or a coordinate:

```clojure
(resolve-jar 'clojure.core '[org.clojure/clojure "1.6.0"])
;; => ["/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar" "clojure/core.clj"]
```

or a vector of coordinates:

```clojure
(resolve-jar 'clojure.core '[[org.clojure/clojure "1.6.0"]])
;; => ["/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar" "clojure/core.clj"]
```

or if you simply just want to explore, the context can be an entire maven local repository:

```clojure
(resolve-jar 'clojure.core :repository)
;; => ["/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar" "clojure/core.clj"]
```

### resolve-coordinates and resolve-with-deps

Once a mapping between the `resource` (path, class or namespace) and the actual jar and jar-entry on the file system, other very helpful functions can be built around `resolve-jar`: 

`resolve-coordinates` works similarly to `resolve-jar` but will return the actual maven-style coordinates

```clojure 
(resolve-coordinates 'version-clj.core)
;; => '[version-clj/version-clj "0.1.2"]

(resolve-coordinates 'clojure.core :repository)
;; => '[org.clojure/clojure "1.6.0"]
```

`resolve-with-deps` will recursively search all child dependencies until it finds

```clojure
(resolve-with-deps
       'clojure.core
       '[vinyasa.maven "0.3.2"])
;;=> ["/Users/zhengc/.m2/repository/org/clojure/clojure/1.6.0/clojure-1.6.0.jar" "clojure/core.clj"]
```


## License

Copyright © 2015 Chris Zheng

Distributed under the MIT License
