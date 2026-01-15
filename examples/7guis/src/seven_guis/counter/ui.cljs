(ns seven-guis.counter.ui
  (:require [circus.module :as module]
            [seven-guis.ui.widgets :as widgets]))

(defn display [count]
  [:div.flex-1.mono.text-right.font-xl.bg-white.px-s.border-ui
   count])

(defn ui [{:keys [count]}]
  (widgets/window
   {:title "Counter"}
   [:div.flex.gap-1
    (display count)
    (widgets/button
     {:on {:click [[::count-incremented]]}}
     "Count")]))

(defmethod module/start ::ui [_ init-count]
  {:count init-count
   :ui #(ui {:count init-count})})

(defmethod module/export ::ui [_ {:keys [ui]}]
  ui)

(defmethod module/tx ::ui [_ {:keys [count] :as ctx} {:keys [type]}]
  (if (= ::count-incremented type)
    (let [n (inc count)]
      (assoc ctx :count n :ui #(ui {:count n})))
    ctx))
