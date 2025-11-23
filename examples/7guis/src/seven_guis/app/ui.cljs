(ns seven-guis.app.ui
  (:require [circus.core :as circus]
            [circus.event :as event]
            [goog.dom :as dom]
            [integrant.core :as ig]
            [replicant.dom :as r]))

(defn ui [{:keys [counter]}]
  [:div.mx-16.my-12
   [:h1.text-4xl.text-gray-700.font-medium.my-8 "7GUIs"]
   (counter)])

(defn- render! [{:keys [el] :as state}]
  (r/render el (ui (select-keys state [:counter]))))

(defmethod ig/init-key ::ui [_ {:keys [root-id tx] :as state}]
  (let [el (dom/getElement root-id)]
    (r/set-dispatch!
     (fn [e data]
       (doseq [[type & xs] data]
         (tx (-> (event/make type xs)
                 (assoc :ui/event e))))))
    (doto (assoc state :el el) render!)))

(defmethod ig/resume-key ::ui [_ _ _ state]
  (doto state render!))

(defmethod circus/tx-key ::ui [_ state _]
  (doto state render!))
