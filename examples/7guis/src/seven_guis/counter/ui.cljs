(ns seven-guis.counter.ui
  (:require [integrant.core :as ig]))

(defn label [count]
  [:div.countdown.font-mono.text-2xl.px-11
   [:span {:aria-label count
           :style {:--value (str count)}}
    count]])

(def button
  [:button.text-lg.font-semibold.rounded-lg.px-6.py-1.text-slate:700.bg-teal-300.hover:bg-teal-400
   {:on {:click [[::count-incremented]]}}
   "Count"])

(defn ui [count]
  [:div.inline-flex.flex-col.gap-5.pb-6.rounded-lg.bg-white.drop-shadow-md.select-none
   {:class "outline-black/5"}
   [:div.flex.items-center.px-2.py-1.gap-2.border-b.border-gray-200.shadow-xs.shadow-slate-50
    (repeat 3 [:div.w-3.h-3.bg-gray-200.rounded-full])
    [:h3.px-2.text-s.text-gray-500 "Counter"]]
   [:div.flex.justify-center.items-center.gap-4.px-6
    (label count)
    button]])

(defmethod ig/init-key ::ui [_ count]
  #(ui count))
