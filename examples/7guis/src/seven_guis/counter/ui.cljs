(ns seven-guis.counter.ui
  (:require [circus.core :as circus]))

(defn window [title & children]
  [:div.inline-flex.flex-col.gap-5.pb-6.rounded-lg.bg-white.drop-shadow-md.select-none
   {:class "outline-black/5"}
   [:div.flex.items-center.px-2.py-1.gap-2.border-b.border-gray-200.shadow-xs.shadow-slate-50
    (repeat 3 [:div.w-3.h-3.bg-gray-200.rounded-full])
    [:h3.px-2.text-s.text-gray-500 title]]
   children])

(defn label [count]
  [:div.font-mono.text-2xl.px-11 count])

(defn button [attrs children]
  [:button.text-lg.font-semibold.rounded-lg.px-6.py-1.text-slate:700.bg-teal-300.hover:bg-teal-400
   attrs
   children])

(defn ui [count]
  (window
   "Counter"
   [:div.flex.justify-center.items-center.gap-4.px-6
    (label count)
    (button
     {:on {:click [[::count-incremented]]}}
     "Count")]))

; (defmethod ig/init-key ::ui [_ init-count]
;   (let [count (atom init-count)]
;     {:count count
;      :ui #(ui @count)}))

; (defmethod ig/resolve-key ::ui [_ {:keys [ui]}]
;   ui)

; (defmethod circus/tx-key ::ui [_ {:keys [count] :as state} {:keys [type]}]
;   (when (= ::count-incremented type)
;     (swap! count inc))
;   state)
