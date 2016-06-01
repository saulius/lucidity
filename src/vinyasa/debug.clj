(ns vinyasa.debug)

(defn wrap-print [f]
  (fn [& args]
    (print " ->" (second (re-find #"\$(.*)@" (str f))) args)
    (apply f args)))

(-> 3 ((wrap-print inc)) ((wrap-print dec)))
;; =>       "-> inc (3) -> dec (4)"

(defmacro dbg-> [n & funcs]
  (println "")
  (print n)
  (let [wfncs (map #(list (list wrap-print %)) funcs)]
     `(-> ~n ~@wfncs )))

(dbg-> 3 inc dec)
;; =>      "3 -> inc (3) -> dec (4)"
