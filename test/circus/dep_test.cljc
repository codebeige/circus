(ns circus.dep-test
  (:require [circus.dep :as dep]
            [circus.generators :as gen*]
            [circus.module :as module]
            [clojure.set :as set]
            [clojure.test :refer [deftest is]]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]))

(deftest resolve-test
  (try
    (defmethod module/export ::module-b [_ ctx]
      (:b ctx))
    (let [x (dep/make ::module-b)
          system {::module-a {:a "A"}
                  ::module-b {:b "B"}}]
      (is (nil? @x))
      (is (= "B" @(dep/resolve x system))))
    (finally
     (remove-method module/export ::module-b))))

(deftest dep?-test
  (is (dep/dep? (dep/make :a)))
  (is (not (dep/dep? {:key :a :val 123}))))

(deftest resolved?-test
  (let [x (dep/make :x)
        system {}]
    (is (not (dep/resolved? x)))
    (is (dep/resolved? (-> x (dep/resolve system))))))

(defn topo-seq-is-superset-of-entry-points-prop [system-ks]
  (prop/for-all [[system ks] system-ks]
    (set/superset? (into #{} (dep/topo-seq system ks))
                   (into #{} ks))))

(defspec topo-seq-is-superset-of-entry-points 100
  (topo-seq-is-superset-of-entry-points-prop gen*/system-ks*))

(comment
 (tc/quick-check 1000
   (topo-seq-is-superset-of-entry-points-prop gen*/system-ks)))

(defn topo-seq-includes-dependencies-prop [system-ks]
  (prop/for-all [[system ks] system-ks]
    ; FIXME: implement
    ))

(defn topo-seq-is-sorted-in-topological-order-prop [system-ks]
  (prop/for-all [[system ks] system-ks]
    ; FIXME: implement
    ))

; TODO: test cycle detection
