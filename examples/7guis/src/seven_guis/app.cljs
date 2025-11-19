(ns seven-guis.app
  (:require [circus.core :as circus]
            [seven-guis.counter.ui :as counter]
            [seven-guis.app.ui :as app]))

(def system
  {::counter/ui 0
   ::app/ui {:counter ::counter/ui
             :root-id "app"}})

(defonce app (circus/make system))

(defn init []
  (circus/start! app))

(comment
 (circus/stop! app))
