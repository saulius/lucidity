(ns lucid.outline
  (:require [hara.test :as test]
            [hara.io.file :as fs]
            [hara.namespace.eval :as eval]
            [hara.string.prose :as prose]
            [clojure.string :as string]))

(defn markdown-code?
  "checks if the string has the beginning/end of markdown code
 
   (markdown-code? \"```\")
   => true
   
   (markdown-code? \"```clojure\" \"clojure\")
   => true"
  {:added "1.2"}
  ([s]
   (.startsWith s "```"))
  ([s x]
   (.startsWith s (str "```" x))))

(defn has-spacing?
  "checks that a string starts with `n` spaces
 
   (has-spacing? \"  hello\" 2)
   => true"
  {:added "1.2"}
  ([s] (.startsWith s " "))
  ([s number]
   (.startsWith s (apply str (repeat number " ")))))

(defn uncomment-arrows
  "uncomments the arrows for actual testing
 
   (uncomment-arrows \";;=>\")
   => \"=>\""
  {:added "1.2"}
  [line]
  (.replaceAll line "\\;+=>" "=>"))

(defn filter-forms
  "grabs only clojure code from lines
   (filter-forms  [[1 \"hello there\"]
                   [2 \"\"]
                   [3 \"    (+ 1 2)\"]
                   [4 \"    => 3\"]])
   => [[3 \"(+ 1 2)\"]
       [4 \"=> 3\"]]"
  {:added "1.2"}
  ([lines] (filter-forms lines "" {} []))
  ([[[num current] & more :as lines] prev state output]
   (cond (empty? lines)
         output

         (or (nil? current)
             (prose/whitespace? current))
         (recur more current state output)

         (:in-clj state)
         (cond (markdown-code? current)
               (recur more current (dissoc state :in-clj) output)

               :else
               (recur more current state (conj output [num current])))

         (prose/whitespace? current)
         (recur more current  state output)

         (and (or (prose/whitespace? prev)
                  (has-spacing? prev 4))
              (has-spacing? current 4))
         (recur more current state (conj output [num (subs current 4)]))

         (markdown-code? current "clojure")
         (recur more current (assoc state :in-clj true)  output)

         :else
         (recur more current state output))))

(defn lines
  "reads a file and numbers each line"
  {:added "1.2"}
  [path]
  (->> (fs/path path)
       (fs/reader)
       (line-seq)
       (map-indexed (fn [i v] [i v]))))

(defn read-forms
  "reads clojure forms from lines
   
   (read-forms  [[1 \"hello there\"]
                 [2 \"\"]
                 [3 \"    (+ 1 2)\"]
                 [4 \"    => 3\"]])
   => '[(+ 1 2) => 3]"
  {:added "1.2"}
  [lines]
  (let [merge-lines (fn [[num line]]
                      (format "^{:line %s} %s" num line))]
    (->> lines
         (filter-forms)
         (map merge-lines)
         (map uncomment-arrows)
         (string/join "\n")
         (#(str "[\n" % "\n]"))
         (read-string))))

(defn fact-form
  "outputs a fact form
   
   (fact-form \"some_test.clj\" ^{:line 2} '(+ 1 2) 3 )
   => (contains
       ['fact string? '(+ 1 2) '=> '3])"
  {:added "1.2"}
  [filename f1 arrow f3]
  (list 'fact
        (format "EXPRESSION `%s` AT LINE %s IN FILE `%s`"
                f1
                (-> f1 meta :line)
                filename)
        f1 arrow f3))

(defn make-fact-forms
  "make fact form if there is an arrow
   
   (make-fact-forms '[(+ 1 1) (+ 1 2) => 3] \"some_test.clj\")
   => (contains-in
       ['(+ 1 1)
        ['fact string? '(+ 1 2) '=> '3]])"
  {:added "1.2"}
  ([forms filename] (make-fact-forms forms filename []))
  ([[f1 f2 f3 & more :as forms] filename output]
     (cond (empty? forms) output

           (= f2 '=>)
           (recur more filename (conj output (fact-form filename f1 f2 f3)))

           :else
           (recur (cons f2 (cons f3 more)) filename (conj output f1)))))

(defn test
  "tests a markdown file"
  {:added "1.2"}
  [path]
  (-> (lines path)
      (read-forms)
      (make-fact-forms path)
      (->> (cons '(use 'hara.test))
           (apply list `eval/with-tmp-ns))
      (prn)
      (with-out-str)
      (read-string)
      (eval)))
