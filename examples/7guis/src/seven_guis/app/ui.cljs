(ns seven-guis.app.ui
  (:require [circus.module :as module]
            [goog.dom :as dom]
            [replicant.dom :as r]))

(defn ui [{:keys [counter temp-converter]}]
  [:div
   [:h1 "7GUIs"]
   (counter)
   (temp-converter)])

(defn- render! [{:keys [counter el temp-converter]}]
  (r/render el (ui {:counter @counter
                    :temp-converter @temp-converter})))

(defmethod module/start ::ui [_ {:keys [root-id] :as ctx}]
  (doto (assoc ctx :el (dom/getElement root-id)) render!))

(defmethod module/tx ::ui [_ ctx _]
  (doto ctx render!))

(defmethod module/resume ::ui [_ ctx]
  (doto ctx render!))
