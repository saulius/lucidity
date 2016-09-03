(ns lucid.flight.reflection
  (:require [hara.reflect :as reflect]
            [hara.reflect.pretty.args :as args]))

(defmacro .%
  [obj]
  `(reflect/class-info ~obj))

(defmacro .%>
  [obj]
  `(reflect/class-hierarchy ~obj))

(defmacro .?
  [obj & selectors]
  `(reflect/query-class ~obj ~(args/args-convert selectors)))

(defmacro .*
  [obj & selectors]
  `(reflect/query-instance ~obj ~(args/args-convert selectors)))

(defmacro .&
  [obj]
  `(reflect/delegate ~obj))

(defmacro .>var
  ([name [class method & selectors]]
   `(reflect/extract-to-var ~(list `symbol (str name)) ~class ~(str method) ~(args/args-convert selectors)))
  ([name objvec & more]
   [`(.>var ~name ~objvec)]
   ~@(map #(cons `.>var %) (partition 2 more))))

(defmacro .>ns
  [ns class & selectors]
  `(reflect/extract-to-ns ~(list `symbol (str ns)) ~class ~(args/args-convert selectors)))

(defmacro .>
  ([obj] obj)
  ([obj method]
   (cond (not (list? method))
         `(.> ~obj (~method))

         (list? method)
         (let [[method & args] method]
           (cond (#{'.* '.? '.% '.%> '.&} method)
                 `(~(symbol (str "vinyasa.reflection/" method)) ~obj ~@args)

                 (and (symbol? method) (.startsWith (name method) "."))
                 `(reflect/apply-element ~obj ~(subs (name method) 1) ~(vec args))

                 (keyword? method)
                 `(or (~method ~obj ~@args)
                      (let [nm# ~(subs (str method) 1)]
                        (if (some #(= % nm#) (reflect/query-instance ~obj [:name]))
                          (reflect/apply-element ~obj nm# ~(vec args)))))

                 :else
                 `(~method ~obj ~@args)))))


  ([obj method & more]
   `(.> (.> ~obj ~method) ~@more)))
