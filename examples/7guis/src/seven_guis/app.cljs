(ns seven-guis.app
  (:require [circus.core :as circus]
            [circus.event :as event]
            [replicant.dom :as r]
            [seven-guis.app.ui :as app]
            [seven-guis.counter.ui :as counter]))

(def config
  {::app/ui {:counter (circus/dep ::counter/ui)
             :root-id "app"}
   ::counter/ui 0})

(defonce system (atom config))

(declare tx)

(defn dispatch! [e data]
  (doseq [[type & xs] data]
    (tx (-> (event/make type xs)
            (assoc :ui/event e)))))

(defn start []
  (r/set-dispatch! dispatch!)
  (swap! system circus/start [::app/ui]))

(defn tx [event]
  (swap! system circus/tx [::app/ui] event))
