(ns seven-guis.app.ui
  (:require [circus.core :as circus]
            [circus.dom :as dom]
            [goog.dom :as gdom]))

(defn ui [{:keys [counter]}]
  [[:h1 "🎪 Circus – 7 GUIs"]
   [:h2 "Counter"]
   counter])

(circus/defmodule ::ui
  (start! [{:keys [root-id] :as state}]
    (-> state
        (assoc :el (.getElementById js/document root-id))
        (circus/with-fx [::init])))
  (stop! [{:keys [el] :as state}]
    (gdom/removeChildren el)
    (dissoc state :el))
  (update! [{:keys [el] :as state}]
    (dom/render! el (-> state (select-keys [:counter]) ui))))
