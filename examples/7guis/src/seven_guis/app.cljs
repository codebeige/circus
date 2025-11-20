(ns seven-guis.app
  (:require [integrant.core :as ig]
            [seven-guis.counter.ui :as counter]
            [seven-guis.app.ui :as app]))

(def config
  {::app/ui {:counter (ig/ref ::counter/ui)
             :root-id "app"}
   ::counter/ui 0})

(defonce system (atom nil))

(defn init []
  (reset! system (ig/init config)))

(defn suspend! []
  (ig/suspend! @system))

(defn resume []
  (swap! system (partial ig/resume config)))

(defmethod ig/resume-key :default [_ _ _ state]
  state)
