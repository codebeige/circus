(ns circus.hiccup-test
  (:require [circus.hiccup :as hiccup]
            [clojure.test :refer [are deftest]]))

(deftest normalize-shortcut-notation-test
  (are [h h*] (= h* (hiccup/normalize h))
    [:div]                              [:div {}]
    [:div#foo]                          [:div {:id "foo"}]
    [:div.bar.baz]                      [:div {:class #{"bar" "baz"}}]
    [:div#foo.bar.baz]                  [:div {:class #{"bar" "baz"} :id "foo"}]
    [:div.bar {:name "baz"}]            [:div {:class #{"bar"} :name "baz"}]
    [:div "foo" "bar" "baz"]            [:div {} "foo" "bar" "baz"]
    [:div {:name "foo"} "bar" "baz"]    [:div {:name "foo"} "bar" "baz"]
    [:div#foo {:id "bar"}]              [:div {:id "bar"}]
    [:div#foo {:id nil}]                [:div {:id "foo"}]
    [:div {:id nil}]                    [:div {}]
    [:div {:aria-hidden false}]         [:div {:aria-hidden false}]
    [:div.foo {:class #{"bar" "baz"}}]  [:div {:class #{"foo" "bar" "baz"}}]
    [:div {:class ["foo" "bar" "baz"]}] [:div {:class #{"foo" "bar" "baz"}}]
    [:div {:class "foo bar baz"}]       [:div {:class #{"foo" "bar" "baz"}}]
    [:div {:class #{"foo" "bar baz"}}]  [:div {:class #{"foo" "bar" "baz"}}]))
