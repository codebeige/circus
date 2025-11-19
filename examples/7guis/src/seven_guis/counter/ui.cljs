(ns seven-guis.counter.ui
  (:require [circus.core :as circus]))

(defn label [count]
  [:div.countdown.font-mono.text-4xl
    [:span {:style {:--value count}
            :aria {:live "polite"
                   :label count}}
     count]])

(def button
  [:button.btn.btn-lg {:on {:click [[::counter-incremented]]}} "Count"])

(defn ui [count]
  [:div.flex.gap-4 (label count) button])

(circus/defmodule ::ui
  (update! [[k] state]
    (cond-> state (= ::counter-incremented k) inc)
  (export [count]
    (ui count))))
