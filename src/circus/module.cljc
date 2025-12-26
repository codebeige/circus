(ns circus.module)

(defmulti export
  "Exports external-facing value for module `k`.
  Receives the module's current state `ctx`. The returned value is accessible
  via `deref` on the resolved `dep`.

  Default: Returns `ctx` unchanged."
  {:arglists '[[k ctx]]}
  (fn [k _] k))

(defmethod export :default [_ ctx] ctx)


(defmulti start
  "Starts module `k`.
  Can have side-effects. Must return the updated module state.

  Default: Returns `ctx` unchanged."
  {:arglists '[[k ctx]]}
  (fn [k _] k))

(defmethod start :default [_ ctx] ctx)

(defmulti tx
  "Transacts `event` on module `k`.
  Can have side-effects. Must return the updated module state.

  Default: Returns `ctx` unchanged."
  {:arglists '[[k ctx event]]}
  (fn [k _ _] k))

(defmethod tx :default [_ ctx _] ctx)
