(ns circus.core-test
  (:require [circus.core :as circus]
            [circus.module :as module]
            [clojure.test :refer [deftest is]]))

(deftest start-test
  (let [calls (atom [])]
    (try
      (defmethod module/start ::module-a [_ {:keys [b] :as ctx}]
        (swap! calls conj ::module-a)
        (-> ctx
            (assoc :started? true)
            (assoc :b-started? (:started? @b))))
      (defmethod module/start ::module-b [_ ctx]
        (swap! calls conj ::module-b)
        (assoc ctx :started? true))
      (let [system (circus/start {::module-a {:a "A"
                                              :b (circus/dep ::module-b)}
                                  ::module-b {:b "B"}}
                                 [::module-a])]
        (is (= [::module-b ::module-a] @calls) "invoke in topological order")
        (is (true? (get-in system [::module-b :started?])) "update dependency")
        (is (true? (get-in system [::module-a :started?])) "update entry point")
        (is (true? (get-in system [::module-a :b-started?])) "resolve dependency"))
      (finally
       (remove-method module/export ::module-a)
       (remove-method module/export ::module-b)))))
