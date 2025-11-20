(ns seven-guis.counter.ui
  (:require [integrant.core :as ig]))

(defn label [count]
  [:div.countdown.font-mono.text-4xl
   [:span {:aria-label count
           :aria-live "polite"
           :style {:--value (str count)}}
    count]])

(def button
  [:button.btn.btn-lg {:on {:click [[::count-incremented]]}} "Count"])

(defn ui [count]
  [:div.flex.gap-4 (label count) button])

(defmethod ig/init-key ::ui [_ count]
  (ui count))
