(ns lucid.outline
  (:require [hara.test :as test]
            [hara.io.file :as fs]
            [hara.namespace.eval :as eval]
            [clojure.string :as string]))

(defn end-block?
  [line]
  (.startsWith line "```"))

(defn has-spacing?
  [line number]
  (.startsWith line (apply str (repeat number " "))))

(defn all-whitespace?
  [line]
  (or (= "" line) (re-find #"^[\s\t]+$" line)))

(defn has-quote?
  [line x]
  (.startsWith line (str "```" x)))

(defn uncomment-arrows
  [line]
  (.replaceAll line "\\;+=> " "=> "))

(defn filter-forms
  ([lines] (filter-forms lines "" {} []))
  ([[[num current] & more :as lines] prev state output]
   (cond (empty? lines)
         output

         (or (nil? current)
             (all-whitespace? current))
         (recur more current state output)

         (:in-clj state)
         (cond (end-block? current)
               (recur more current (dissoc state :in-clj) output)

               :else
               (recur more current state (conj output [num current])))

         (all-whitespace? current)
         (recur more current  state output)

         (and (or (all-whitespace? prev)
                  (has-spacing? prev 4))
              (has-spacing? current 4))
         (recur more current state (conj output [num (subs current 4)]))

         (has-quote? current "clojure")
         (recur more current (assoc state :in-clj true)  output)

         :else
         (recur more current state output))))

(defn read-forms
  [path]
  (let [merge-lines (fn [[num line]]
                      (format "^{:line %s} %s" num line))
        lines (->> (fs/path path)
                   (fs/reader)
                   (line-seq)
                   (map-indexed (fn [i v] [i v])))]
    (->> lines
         (filter-forms)
         (map merge-lines)
         (map uncomment-arrows)
         (string/join "\n")
         (#(str "[\n" % "\n]"))
         (read-string))))

(defn fact-form
  [filename f1 f2 f3]
  (list 'fact
        (format "EXPRESSION `%s` AT LINE %s IN FILE `%s`"
                f1
                (-> f1 meta :line)
                filename)
        f1 f2 f3))

(defn make-fact-forms
  ([forms filename] (make-fact-forms forms filename []))
  ([[f1 f2 f3 & more :as forms] filename output]
     (cond (empty? forms) output

           (= f2 '=>)
           (recur more filename (conj output (fact-form filename f1 f2 f3)))

           :else
           (recur (cons f2 (cons f3 more)) filename (conj output f1)))))

(defn test
  [path]
  (-> (read-forms path)
      (make-fact-forms path)
      (->> (cons '(use 'hara.test))
           (apply list `eval/with-tmp-ns))
      (prn)
      (with-out-str)
      (read-string)
      (eval)))

(comment
  (lucid.unit/scaffold))
