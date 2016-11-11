(ns lucid.distribute.util.sort)

(defn all-branch-nodes
  "returns all nodes in the branche
 
   (all-branch-nodes MANIFEST)
   => ({:coordinate [blah/blah.common \"0.1.0-SNAPSHOT\"],
        :dependencies [[org.clojure/clojure \"1.6.0\"]],
        :id \"common\"}
       
       ...

       {:coordinate [blah/blah.resources \"0.1.0-SNAPSHOT\"],
        :dependencies [[org.clojure/clojure \"1.6.0\"]],
        :id \"resources\"})"
  {:added "1.2"}
  [manifest]
  (->> (:branches manifest)
       (map (fn [[k m]]
              (-> m
                  (select-keys [:coordinate :dependencies])
                  (assoc :id k))))))

(defn all-branch-deps
  "returns all internal dependencies
 
   (all-branch-deps MANIFEST)
   => #{[blah/blah.util.data \"0.1.0-SNAPSHOT\"]
        [blah/blah.util.array \"0.1.0-SNAPSHOT\"]
        [blah/blah.resources \"0.1.0-SNAPSHOT\"]
        [blah/blah.web \"0.1.0-SNAPSHOT\"]
        [blah/blah.core \"0.1.0-SNAPSHOT\"]
       [blah/blah.common \"0.1.0-SNAPSHOT\"]
        [blah/blah.jvm \"0.1.0-SNAPSHOT\"]}"
  {:added "1.2"}
  [manifest]
  (->> (:branches manifest)
       (map (fn [[k m]] (:coordinate m)))
       (set)))

(defn topsort-branch-deps-pass
  "single topsort pass"
  {:added "1.2"}
  [all sl]
  (reduce (fn [out i]
            (if (some all (:dependencies i))
              out
              (conj out i))) [] sl))

(defn topsort-branch-deps
  "sorts and arranges dependencies in order of deployment
 
   (topsort-branch-deps MANIFEST)
   => [[{:coordinate [blah/blah.common \"0.1.0-SNAPSHOT\"],
         :dependencies [[org.clojure/clojure \"1.6.0\"]],
         :id \"common\"}
        
        ...
       
        {:coordinate [blah/blah.web \"0.1.0-SNAPSHOT\"],
         :dependencies [[org.clojure/clojure \"1.6.0\"]
                        [blah/blah.core \"0.1.0-SNAPSHOT\"]
                        [blah/blah.util.array \"0.1.0-SNAPSHOT\"]
                        [blah/blah.common \"0.1.0-SNAPSHOT\"]],
         :id \"web\"}]]"
  {:added "1.2"}
  [manifest]
  (let [sl  (all-branch-nodes manifest)
        all (all-branch-deps manifest)]
    (loop [all all
           sl  sl
           output []]
      (if-not (or (empty? sl)
                  (empty? all))
        (let [pass (topsort-branch-deps-pass all sl)]
          (recur
           (apply disj all (map :coordinate pass))
           (filter (fn [x] (some #(not= x %) pass) ) sl)
           (conj output pass)))
        output))))
