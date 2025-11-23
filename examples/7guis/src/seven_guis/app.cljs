(ns seven-guis.app
  (:require [circus.core :as circus]
            [integrant.core :as ig]
            [seven-guis.counter.ui :as counter]
            [seven-guis.app.ui :as app]))

(defonce system (atom nil))

(def config
  {::app/ui {:counter (ig/ref ::counter/ui)
             :tx #(circus/tx @system %1)
             :root-id "app"}
   ::counter/ui 0})

; disable default behaviour
(defmethod ig/resume-key :default [_ _ _ state]
  state)

#_{:clojure-lsp/ignore [:clojure-lsp/unused-public-var]}
(defn init []
  (reset! system (ig/init config)))

#_{:clojure-lsp/ignore [:clojure-lsp/unused-public-var]}
(defn suspend! []
  (ig/suspend! @system))

#_{:clojure-lsp/ignore [:clojure-lsp/unused-public-var]}
(defn resume []
  (swap! system (partial ig/resume config)))
