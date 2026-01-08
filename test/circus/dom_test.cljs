(ns circus.dom-test
  (:require [circus.dom :as dom]
            [clojure.test :refer [deftest is]]))

(deftest render-raises-on-tag-mismatch-test
  (let [n (.createElement js/document "div")]
    (is (thrown-with-msg? js/Error
                          #"expected :div, but got :span"
                          (dom/render! n [:span {}])))))

(deftest render-updates-el-attributes-test
  (let [n (doto (.createElement js/document "div")
            (.setAttribute "class" "foo")
            (.setAttribute "data-my-val" "123"))]
    (dom/render! n [:div {:class "bar" :data-my-new-val "456"}])
    (is (= "bar" (.getAttribute n "class")) "update attribute")
    (is (= "456" (.getAttribute n "data-my-new-val")) "add attribute")
    (is (nil? (.getAttribute n "data-my-val")) "remove attribute")))

; TODO: update, add, remove boolean attributes (e.g., disabled) in expand?

(comment
  (let [n (doto (.createElement js/document "div")
            (.setAttribute "class" "foo")
            (.setAttribute "data-my-val" 123))]
    (.getAttribute n "disabled" ))
  )
