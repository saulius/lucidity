(ns lucid.unit.zipper
  (:require [clojure.zip :as zip]
            [clojure.string :as string]
            [lucid.query :as query]
            [rewrite-clj
             [node :as node]
             [zip :as source]]))

(defn append-node
  "Adds node as well as whitespace and newline on right
 
   (-> (z/of-string \"(+)\")
       (z/down)
       (append-node 2)
       (append-node 1)
       (z/->root-string))
   => \"(+\\n  1\\n  2)\""
  {:added "0.1"}
  [zloc node]
  (if node
    (-> zloc
        (zip/insert-right node)
        (zip/insert-right (node/whitespace-node "  "))
        (zip/insert-right (node/newline-node "\n")))
    zloc))

(defn has-quotes?
  "checks if a string has quotes
 
   (has-quotes? \"\\\"hello\\\"\")
   => true"
  {:added "0.1"}
  [s]
  (and (.startsWith s "\"")
           (.endsWith s "\"")))

(defn strip-quotes
  "gets rid of quotes in a string
 
   (strip-quotes \"\\\"hello\\\"\")
   => \"hello\""
  {:added "0.1"}
  [s]
  (if (has-quotes? s) 
    (subs s 1 (dec (count s)))
    s))

(defn escape-newlines
  "makes sure that newlines are printable
 
   (escape-newlines \"\\\n\")
   => \"\\n\""
  {:added "0.1"}
  [s]
  (-> s
      (.replaceAll "\\n" "\\\\n")))

(defn escape-escapes
  "makes sure that newlines are printable
 
   (escape-escapes \"\\\n\")
   => \"\\\n\""
  {:added "0.1"}
  [s]
  (-> s
      (.replaceAll "(\\\\)([A-Za-z])" "$1$1$2")))

(defn escape-quotes
  "makes sure that quotes are printable in string form
 
   (escape-quotes \"\\\"hello\\\"\")
   => \"\\\"hello\\\"\""
  {:added "0.1"}
  [s]
  (-> s
      (.replaceAll "(\\\\)?\"" "$1$1\\\\\\\"")))

(defn strip-quotes-array
  "utility that strips quotes when not the result of a fact
   (strip-quotes-array [\"\\\"hello\\\"\"])
   => [\"hello\"]
   
   (strip-quotes-array [\"(str \\\"hello\\\")\" \" \" \"=>\" \" \" \"\\\"hello\\\"\"])
   => [\"(str \\\"hello\\\")\" \" \" \"=>\" \" \" \"\\\"hello\\\"\"]"
  {:added "0.1"}
  ([arr] (strip-quotes-array arr nil nil []))
  ([[x & more] p1 p2 out]
   (cond (nil? x)
         out

         :else
         (recur more x p1 (conj out (if (= p2 "=>")
                                      (if (has-quotes? x)
                                        (escape-newlines x)
                                        x)
                                      (strip-quotes x)))))))

(defn nodes->docstring
  "converts nodes to a docstring compatible
   (->> (z/of-string \"\\\"hello\\\"\\n  (+ 1 2)\\n => 3 \")
        (iterate z/right*)
        (take-while identity)
        (map z/node)
        (nodes->docstring)
        (node/string))
   => \"\"hello\\n  (+ 1 2)\\n  => 3 \"\"
 
   (->> (z/of-string (str [\\e \\d]))
        (iterate z/right*)
        (take-while identity)
        (map z/node)
        (nodes->docstring)
        (str)
        (read-string))
  => \"[\\e \\d]\""
  {:added "0.1"}
  [nodes]
  (->> nodes
       (map node/string)
       (strip-quotes-array)
       (string/join)
       (escape-escapes)
       (escape-quotes)
       (string/split-lines)
       (map-indexed (fn [i s]
                      (str (if-not (or (zero? i)
                                       (= i (dec (count nodes))))
                             " ")
                           s)))
       (node/string-node)))

(defn insert
  "inserts the meta information and docstring from tests"
  {:added "0.1"}
  [zloc nsp gathered]
  (let [sym   (source/sexpr zloc)
        nodes (get-in gathered [nsp sym :docs])
        meta  (get-in gathered [nsp sym :meta])]
    (-> zloc
        (append-node meta)
        (append-node (if nodes (nodes->docstring nodes))))))

(defn write-file
  "exports the zipper contents to file"
  {:added "0.1"}
  [zloc file]
  (->> (iterate source/right* zloc)
       (take-while identity)
       (map source/node)
       (map node/string)
       (string/join)
       (spit file)))

(defn selector
  "builds a selector for functions
 
   (selector 'hello)
   => '[(#{defn defmacro defmulti} | hello ^:%?- string? ^:%?- map? & _)]"
  {:added "0.1"}
  ([] (selector nil))
  ([var]
   [(list '#{defn defmacro defmulti} '| (or var '_) '^:%?- string? '^:%?- map? '& '_)]))

(defn walk-file
  "helper function for file manipulation used by import and purge"
  {:added "0.1"}
  [file var references action-fn]
  (let [zloc (source/of-file file)
        nsp  (-> (query/$ zloc [(ns | _ & _)] {:walk :top})
                 first)
        action (action-fn nsp references)
        zloc (-> zloc
                 (query/modify (selector var)
                               action
                               {:walk :top}))]
    (write-file zloc file)))
