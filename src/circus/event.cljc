(ns circus.event)

(defrecord Event [type payload])

(defn make [type payload]
  (->Event type payload))
