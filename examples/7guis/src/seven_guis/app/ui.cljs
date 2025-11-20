(ns seven-guis.app.ui
  (:require [goog.dom :as dom]
            [integrant.core :as ig]
            [replicant.dom :as r]))

(defn ui [{:keys [counter]}]
  [:div.mx-16.my-12
   [:h1.text-4xl.text-gray-700.font-medium.my-8 "7GUIs"]
   (counter)])

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
