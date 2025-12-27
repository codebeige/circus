(ns circus.core-test
  (:require [circus.core :as circus]
            [circus.event :as event]
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
       (remove-method module/start ::module-a)
       (remove-method module/start ::module-b)))))

(deftest stop-test
  (let [calls (atom [])]
    (try
      (defmethod module/stop ::module-a [_ {:keys [b] :as ctx}]
        (swap! calls conj ::module-a)
        (-> ctx
            (assoc :stopped? true)
            (assoc :b-stopped? (:stopped? @b))))
      (defmethod module/stop ::module-b [_ ctx]
        (swap! calls conj ::module-b)
        (assoc ctx :stopped? true))
      (let [system (circus/stop {::module-a {:a "A"
                                             :b (circus/dep ::module-b)}
                                 ::module-b {:b "B"}}
                                [::module-a])]
        (is (= [::module-a ::module-b] @calls) "invoke in reverse topological order")
        (is (true? (get-in system [::module-a :stopped?])) "update entry point")
        (is (nil? (get-in system [::module-a :b-stopped?])) "resolve dependency with old state")
        (is (true? (get-in system [::module-b :stopped?])) "update dependency"))
      (finally
        (remove-method module/stop ::module-a)
        (remove-method module/stop ::module-b)))))

(deftest tx-test
  (let [calls (atom [])]
    (try
      (defmethod module/tx ::module-a [_ {:keys [b] :as ctx} e]
        (swap! calls conj ::module-a)
        (-> ctx
            (assoc :event e)
            (assoc :b-event (:event @b))))
      (defmethod module/tx ::module-b [_ ctx e]
        (swap! calls conj ::module-b)
        (assoc ctx :event e))
      (let [event (event/make :test {:foo "bar"})
            system (circus/tx {::module-a {:a "A"
                                           :b (circus/dep ::module-b)}
                               ::module-b {:b "B"}}
                              [::module-a]
                              event)]
        (is (= [::module-b ::module-a] @calls) "invoke in topological order")
        (is (= event (get-in system [::module-b :event])) "update dependency")
        (is (= event (get-in system [::module-a :event])) "update entry point")
        (is (= event (get-in system [::module-a :b-event])) "resolve dependency"))
      (finally
        (remove-method module/tx ::module-a)
        (remove-method module/tx ::module-b)))))

(deftest suspend-test
  (let [calls (atom [])]
    (try
      (defmethod module/suspend ::module-a [_ {:keys [b] :as ctx}]
        (swap! calls conj ::module-a)
        (-> ctx
            (assoc :suspended? true)
            (assoc :b-suspended? (:suspended? @b))))
      (defmethod module/suspend ::module-b [_ ctx]
        (swap! calls conj ::module-b)
        (assoc ctx :suspended? true))
      (let [system (circus/suspend {::module-a {:a "A"
                                                :b (circus/dep ::module-b)}
                                    ::module-b {:b "B"}}
                                   [::module-a])]
        (is (= [::module-a ::module-b] @calls) "invoke in reverse topological order")
        (is (true? (get-in system [::module-a :suspended?])) "update entry point")
        (is (nil? (get-in system [::module-a :b-suspended?])) "resolve dependency with old state")
        (is (true? (get-in system [::module-b :suspended?])) "update dependency"))
      (finally
        (remove-method module/suspend ::module-a)
        (remove-method module/suspend ::module-b)))))

(deftest resume-test
  (let [calls (atom [])]
    (try
      (defmethod module/resume ::module-a [_ {:keys [b] :as ctx}]
        (swap! calls conj ::module-a)
        (-> ctx
            (assoc :resumeed? true)
            (assoc :b-resumeed? (:resumeed? @b))))
      (defmethod module/resume ::module-b [_ ctx]
        (swap! calls conj ::module-b)
        (assoc ctx :resumeed? true))
      (let [system (circus/resume {::module-a {:a "A"
                                              :b (circus/dep ::module-b)}
                                  ::module-b {:b "B"}}
                                 [::module-a])]
        (is (= [::module-b ::module-a] @calls) "invoke in topological order")
        (is (true? (get-in system [::module-b :resumeed?])) "update dependency")
        (is (true? (get-in system [::module-a :resumeed?])) "update entry point")
        (is (true? (get-in system [::module-a :b-resumeed?])) "resolve dependency"))
      (finally
       (remove-method module/resume ::module-a)
       (remove-method module/resume ::module-b)))))
