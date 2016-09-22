(ns lucid.core.debug)

(defn wrap-print
  "wraps a function in a `println` statement'
 
   ((lucid.core.debug/wrap-print + (quote (+ 1 2 3)) (quote ->)) 1 2 3)
   => 6
 
   (with-out-str
     ((lucid.core.debug/wrap-print + (quote (+ 1 2 3)) (quote ->)) 1 2 3))
   => \"-> (+ 1 2 3) :: 6\""
  {:added "1.1"}
  [f expr arrow]
  (fn [& args]
    (let [result (apply f args)]
      (println arrow expr "::" result)
      result)))

(defn dbg-print
  "creates the form for debug print
 
   (dbg-print '(+ 1 2 3) '->)
   => '((lucid.core.debug/wrap-print + (quote (+ 1 2 3)) (quote ->)) 1 2 3)"
  {:added "1.1"}
  [form arrow]
  (cond (list? form)
        `((wrap-print ~(first form)
                      (quote ~form)
                      (quote ~arrow))
          ~@(rest form))

        :else
        `((wrap-print ~form (quote ~form) (quote ~arrow)))))

(defmacro dbg->
  "prints each stage of the `->` macro
   (-> (dbg-> {:a 1}
                        (assoc :b 2)
                        (merge {:c 3}))
       (with-out-str)
       (string/split-lines))
   => [\"\" \"\"
       \"{:a 1}\"
      \"-> (assoc :b 2) :: {:a 1, :b 2}\"
       \"-> (merge {:c 3}) :: {:a 1, :b 2, :c 3}\"]"
  {:added "1.1"}
  [n & funcs]
  (let [wfncs (map #(dbg-print % '->) funcs)]
    `(do (println "\n")
         (println ~n)
         (-> ~n ~@wfncs))))

(defmacro dbg->>
  "prints each stage of the `->>` macro
   
   (->  (dbg->> (range 5)
                (map inc)
                (take 2))
       (with-out-str)
       (string/split-lines))
   => [\"\" \"\"
       \"(0 1 2 3 4)\"
       \"->> (map inc) :: (1 2 3 4 5)\"
      \"->> (take 2) :: (1 2)\"]"
  {:added "1.1"}
  [n & funcs]
  (let [wfncs (map #(dbg-print % '->>) funcs)]
    `(do (println "\n")
         (println ~n)
         (->> ~n ~@wfncs))))
