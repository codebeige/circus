(ns seven-guis.app
  (:require [integrant.core :as ig]
            [seven-guis.counter.ui :as counter]
            [seven-guis.app.ui :as app]))

(def config
  {::app/ui {:counter (ig/ref ::counter/ui)
             :root-id "app"}
   ::counter/ui 0})

(def system (atom nil))

#_{:clojure-lsp/ignore [:clojure-lsp/unused-public-var]}
(defn init []
  (reset! system (ig/init config)))

(comment
 (init)
 (ig/halt! @system))
