(ns seven-guis.app.ui
  (:require [circus.module :as module]
            [goog.dom :as dom]
            [replicant.dom :as r]))

(defn ui [{:keys [counter]}]
  [:div
   [:h1 "7GUIs"]
   (counter)])

(defn- render! [{:keys [counter el]}]
  (r/render el (ui {:counter @counter})))

(defmethod module/start ::ui [_ {:keys [root-id] :as ctx}]
  (doto (assoc ctx :el (dom/getElement root-id)) render!))

(defmethod module/tx ::ui [_ ctx _]
  (doto ctx render!))

(defmethod module/resume ::ui [_ ctx]
  (doto ctx render!))
