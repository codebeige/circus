(ns circus.core
  (:require [circus.dep :as dep]
            [circus.module :as module]
            [clojure.walk :as walk]))

(defn dep
  "Creates and returns a dependency referring to the module with `key`.

  Use [[deref]] to access the currently exported value."
  [key]
  (dep/make key))

(defn- resolve-deps [f system]
  (walk/postwalk #(cond-> % (dep/dep? %) (dep/resolve system)) f))

(defn tx
  "Returns a new system state by updating each module with key in `ks` and its
  dependencies invoking `f` on the key and module state in topological order.

  Dependencies are resolved with the current system state before passing the
  module state to `f`."
  ([system f] (tx system f (keys system)))
  ([system f ks]
   (reduce (fn [system k]
             (update system k #(f k (resolve-deps % system))))
           system
           (dep/topo-seq system ks))))

(defn start
  "Returns a new system state by updating each module with key in `ks` and its
  dependencies by invoking `module/start` for each key in topological order.

  Dependencies are resolved with the current system state before passing the
  module state to `f`."
  {:arglists '[[system] [system ks]]}
  [system & args]
  (apply tx system module/start args))
