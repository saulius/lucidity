(ns lucid.flight.debug)

(defn wrap-print [f expr arrow]
  (fn [& args]
    (let [result (apply f args)]
      (println arrow expr "::" result)
      result)))

(defn dbg-helper [form arrow]
  (cond (list? form)
        `((wrap-print ~(first form)
                      (quote ~form)
                      (quote ~arrow))
          ~@(rest form))

        :else
        `((wrap-print ~form (quote ~form) (quote ~arrow)))))

(defmacro dbg-> [n & funcs]
  (let [wfncs (map #(dbg-helper % '->) funcs)]
    `(do (println "\n")
         (println ~n)
         (-> ~n ~@wfncs))))

(defmacro dbg->> [n & funcs]
  (let [wfncs (map #(dbg-helper % '->>) funcs)]
    `(do (println "\n")
         (println ~n)
         (->> ~n ~@wfncs))))
