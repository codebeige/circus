(ns circus.generators
  "Generator variants marked with a trailing star (e.g., `system*`) produce
  simplified, faster, and more human-readable output. Their counterparts,
  however, cover all valid values to catch edge cases during testing."
  (:require [circus.dep :as dep]
            [clojure.test.check.generators :as gen]))

(def topology
  (gen/bind
   (gen/sized #(gen/choose 0 (min % 20)))
   (fn [n]
     (gen/shuffle (range n)))))

(defn max-verts [n]
  (/ (* n (- n 1)) 2))

(defn verts [topo]
  (let [n (count topo)
        max-vts (min (max-verts n) (* 2 n))]
    (if (pos? max-vts)
      (gen/set
       (gen/fmap
        sort
        (gen/such-that
         (partial apply not=)
         (gen/tuple (gen/elements topo) (gen/elements topo))))
       {:max-elements max-vts})
      (gen/return #{}))))

(def dag
  (gen/let [topo topology
            vts (verts topo)]
    {:nodes (set topo)
     :verts vts}))

(def ^:private keys*
  (mapv (comp keyword str) "abcdefghijklmnopqrstuvwxyz"))

(defn key* [n]
  (let [l (count keys*)]
    (if (< n l)
      (nth keys* n)
      (keyword (str (name (key* (- n l))) "'")))))

(defn mod-key* [n]
  (keyword (str "module-" (name (key* n)))))

(def system*
  (gen/let [{:keys [nodes verts]} dag]
    (reduce
     (fn [m [n1 n2]]
       (update m (mod-key* n1) assoc (key* n2) (dep/make (mod-key* n2))))
     (zipmap (map mod-key* nodes) (repeat {}))
     verts)))

(defn assoc-any [x k v]
  (assoc (cond->> x (not (map? x)) (hash-map ::value)) k v))

(def system
  (gen/let [{:keys [nodes verts]} dag
            mods (gen/vector
                  (gen/resize
                   10
                   (gen/tuple
                    (gen/one-of [gen/keyword gen/keyword-ns])
                    (gen/frequency
                     [[8 (gen/map gen/any-equatable gen/any {:max-elements 5})]
                      [2 gen/any]])))
                  (count nodes))]
    (let [mod-key (fn [n] (first (nth mods n)))]
      (reduce
       (fn [m [n1 n2]]
         (update m (mod-key n1) assoc-any (key* n2) (dep/make (mod-key n2))))
       (into {} mods)
       verts))))

(defn entry-points [m]
  (let [ks (keys m)]
    (if (empty? ks)
      (gen/return [])
      (gen/vector-distinct
       (gen/elements ks)
       {:min-elements 1
        :max-elements (count ks)}))))

(def system-ks*
  (gen/let [m system*
            ks (entry-points m)]
    [m ks]))

(def system-ks
  (gen/let [m system
            ks (entry-points m)]
    [m ks]))
