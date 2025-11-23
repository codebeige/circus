(ns circus.core
  (:require [integrant.core :as ig])
  (:refer-clojure :exclude [update]))

(defmulti tx-key
  {:arglists '([key system event])}
  (fn [k _ _] (ig/normalize-key k)))

(defmethod tx-key :default [_ system _]
  system)

(defn tx
  ([system e]
   (tx system (keys system) e))
  ([system ks e]
   (ig/run! system ks #(tx-key %1 %2 e))))
