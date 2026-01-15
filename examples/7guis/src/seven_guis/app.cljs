(ns seven-guis.app
  (:require [circus.core :as circus]
            [circus.event :as event]
            [replicant.dom :as r]
            [seven-guis.app.ui :as app]
            [seven-guis.counter.ui :as counter]
            [seven-guis.temp-converter.ui :as temp-converter]))

(def config
  {::app/ui {:counter (circus/dep ::counter/ui)
             :root-id "app"
             :temp-converter (circus/dep ::temp-converter/ui)}
   ::counter/ui 0
   ::temp-converter/ui {:temp-fahrenheit nil}})

(defonce system (atom config))

(declare tx)

(defn dispatch! [e data]
  (doseq [[type & xs] data]
    (tx (-> (event/make type xs)
            (assoc :ui/event e)))))

#_{:clojure-lsp/ignore [:clojure-lsp/unused-public-var]}
(defn start []
  (r/set-dispatch! dispatch!)
  (swap! system circus/start [::app/ui]))

(defn tx [event]
  (swap! system circus/tx [::app/ui] event))

#_{:clojure-lsp/ignore [:clojure-lsp/unused-public-var]}
(defn suspend []
  (swap! system circus/suspend [::app/ui]))

#_{:clojure-lsp/ignore [:clojure-lsp/unused-public-var]}
(defn resume []
  (swap! system circus/resume [::app/ui]))

@system
