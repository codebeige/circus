(ns circus.core
  (:require [circus.dep :as dep]))

(defn dep
  "Creates and returns a dependency referring to the module with `key`.

  Use [[deref]] to access the currently exported value."
  [key]
  (dep/make key))
