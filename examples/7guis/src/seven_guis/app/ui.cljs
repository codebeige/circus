(ns seven-guis.app.ui
  (:require [goog.dom :as dom]
            [integrant.core :as ig]
            [replicant.dom :as r]))

(defn ui [{:keys [counter]}]
  [:div
   [:h1 "🎪 Circus – 7 GUIs"]
   [:h2 "Counter"]
   counter])

(defn dispatch! [_ data]
  (doseq [d data] (js/pr d)))

(defmethod ig/init-key ::ui [_ {:keys [root-id] :as state}]
  (let [el (dom/getElement root-id)]
    (r/set-dispatch! dispatch!)
    (r/render el (ui (select-keys state [:counter])))
    (assoc state :el el)))

(defmethod ig/resume-key ::ui [_ _ _ {:keys [el] :as state}]
  (r/render el (ui (select-keys state [:counter])))
  state)
