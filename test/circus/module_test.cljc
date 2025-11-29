(ns circus.module-test
  (:require [circus.module :as module]
            [clojure.test :refer [deftest is]]))

(deftest default-export-test
  (let [ctx {:a "A" :b "B" :c "C"}]
    (is (= ctx (module/export ::some-module ctx)))))
