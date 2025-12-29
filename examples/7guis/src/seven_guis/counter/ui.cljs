(ns seven-guis.counter.ui
  (:require [circus.module :as module]))

(defn window [title & children]
  [:div
   {:class "outline-black/5"}
   [:div
    (repeat 3 [:div])
    [:h3 title]]
   children])

(defn label [count]
  [:div count])

(defn button [attrs children]
  [:button
   attrs
   children])

(defn ui [count]
  (window
   "Counter"
   [:div
    (label count)
    (button
     {:on {:click [[::count-incremented]]}}
     "Count")]))

(defmethod module/start ::ui [_ init-count]
  {:count init-count
   :ui #(ui init-count)})

(defmethod module/export ::ui [_ {:keys [ui]}]
  ui)

(defmethod module/tx ::ui [_ {:keys [count] :as ctx} {:keys [type]}]
  (if (= ::count-incremented type)
    (let [n (inc count)]
      (assoc ctx :count n :ui #(ui n)))
    ctx))
