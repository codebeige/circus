(ns circus.dep-test
  (:require [circus.dep :as dep]
            [circus.generators :as gen*]
            [circus.module :as module]
            [clojure.set :as set]
            [clojure.test :refer [are deftest is]]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop])
  #?(:clj (:import [clojure.lang ExceptionInfo])))

(deftest resolve-test
  (try
    (defmethod module/export ::module-b [_ ctx]
      (:b ctx))
    (let [x (dep/make ::module-b)
          system {::module-a {:a "A"}
                  ::module-b {:b "B"}}]
      (is (nil? @x) "nil when unresolved")
      (is (= "B" @(dep/resolve x system)) "resolve with export for key"))
    (finally
      (remove-method module/export ::module-b))))

(deftest dep?-test
  (is (dep/dep? (dep/make :a)))
  (is (not (dep/dep? {:key :a :val 123}))))

(deftest deps-test
  (are [ks f] (= ks (sort (dep/deps f)))
    [] nil
    [] 123
    [] {}
    [:foo] (dep/make :foo)
    [:bar] [:foo (dep/make :bar) :baz]
    [:baz] {:foo {:bar (dep/make :baz)}}
    [:a] [(dep/make :a) (dep/make :a) (dep/make :a)]
    [:x :y :z] {:a (dep/make :x)
                :b (dep/make :y)
                :c (dep/make :z)}))

(deftest deps?-test
  (are [deps? f] (= deps? (some? (dep/deps? f)))
    false nil
    false 123
    false {}
    true {:a (dep/make :x)
          :b (dep/make :y)
          :c (dep/make :z)}
    true {:foo {:bar (dep/make :baz)}}
    true [:foo (dep/make :bar) :baz]
    true (dep/make :foo)
    true [(dep/make :a) (dep/make :a) (dep/make :a)]))

(deftest topo-seq-aborts-on-cyclic-dep-test
  (are [system ks] (dep/ex-cyclic-dep?
                    (try
                      (doall (dep/topo-seq system))
                      :not-thrown
                      (catch ExceptionInfo ex
                        ex)))
    {:A {:self (dep/make :A)}} [:A]
    {:A {:b (dep/make :B)}
     :B {:c (dep/make :C)}
     :C {:a (dep/make :A)}} [:A]))

(defn topo-seq-includes-entry-points-prop [system-ks]
  (prop/for-all [[system ks] system-ks]
    (is
     (set/subset? (into #{} ks)
                  (into #{} (dep/topo-seq system ks))))))

(defspec topo-seq-includes-entry-points 100
  (topo-seq-includes-entry-points-prop gen*/system-ks*))

(comment
  (tc/quick-check 1000
    (topo-seq-includes-entry-points-prop gen*/system-ks)))

(defn topo-seq-includes-dependencies-prop [system-ks]
  (prop/for-all [[system ks] system-ks]
    (is
     (set/subset? (into #{} (mapcat #(dep/deps (get system %))) ks)
                  (into #{} (dep/topo-seq system ks))))))

(defspec topo-seq-includes-dependencies 100
  (topo-seq-includes-dependencies-prop gen*/system-ks*))

(comment
  (tc/quick-check 1000
    (topo-seq-includes-dependencies-prop gen*/system-ks)))

(defn topo-seq-is-sorted-in-topological-order-prop [system-ks]
  (prop/for-all [[system ks] system-ks]
    (let [ks* (dep/topo-seq system ks)]
      (is
       (every?
        (fn [[visited k]]
          (set/superset?
           (into #{} visited)
           (into #{} (dep/deps (get system k)))))
        (map-indexed (fn [i k] [(take i ks*) k]) ks*))))))

(defspec topo-seq-is-sorted-in-topological-order 100
  (topo-seq-is-sorted-in-topological-order-prop gen*/system-ks*))

(comment
  (tc/quick-check 1000
    (topo-seq-is-sorted-in-topological-order-prop gen*/system-ks)))
