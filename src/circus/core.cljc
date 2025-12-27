(ns circus.core
  (:require [circus.dep :as dep]
            [circus.module :as module]
            [clojure.walk :as walk]))

(defn dep
  "Creates and returns a dependency referring to the module with key `k`.
  Use `deref` to access the value returned by `module/export`."
  [k]
  (dep/make k))

(defn- resolve-deps [f system]
  (walk/postwalk #(cond-> % (dep/dep? %) (dep/resolve system)) f))

(defn- invoke* [system ks f args]
  (reduce (fn [system k]
            (update system k #(apply f k (resolve-deps % system) args)))
          system
          ks))

(defn invoke
  "Invokes `f` on `ks` an all their dependencies.
  Calls `f` for each relevant module in topological order. `f` receives the
  module key, the module state with resolved and updated dependencies, and
  `args`. Returns a new `system` with module updates applied."
  [system ks f & args]
  (invoke* system (dep/topo-seq system ks) f args))

(defn invoke-rev
  "Invokes `f` on `ks` an all their dependencies in *reverse* order.
  Calls `f` for each relevant module in reverse topological order. `f` receives
  the module key, the module state with resolved dependencies before they are
  updated, and `args`. Returns a new `system` with module updates applied."
  [system ks f & args]
  (invoke* system (reverse (dep/topo-seq system ks)) f args))

(defn start
  "Starts `ks` and all their dependencies.
  Invokes `module/start` on each relevant module in topological order. Returns
  a new `system` with module updates applied."
  [system ks]
  (invoke system ks module/start))

(defn stop
  "Stops `ks` and all their dependencies.
  Invokes `module/stop` on each relevant module in reverse topological order.
  Returns a new `system` with module updates applied."
  [system ks]
  (invoke-rev system ks module/stop))

(defn tx
  "Transacts `event` on `ks` and all their dependencies.
  Invokes `module/tx` for each relevant module in topological order. Returns a
  new `system` with module updates applied."
  [system ks event]
  (invoke system ks module/tx event))

(defn suspend
  "Suspends `ks` and all their dependencies.
  Invokes `module/suspend` on each relevant module in reverse topological order.
  Returns a new `system` with module updates applied."
  [system ks]
  (invoke-rev system ks module/suspend))

(defn resume
  "Resumes `ks` and all their dependencies.
  Invokes `module/resume` on each relevant module in topological order. Returns
  a new `system` with module updates applied."
  [system ks]
  (invoke system ks module/resume))
