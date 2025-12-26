(ns circus.module)

(defmulti export
  "Returns external-facing value of module with key `k` derived from its
  current state `ctx`.

  Default: Returns `ctx` unchanged."
  {:arglists '[[k ctx]]}
  (fn [k _] k))

(defmethod export :default [_ ctx] ctx)


(defmulti start
  "Returns updated state for module with key `k` by invoking `start` with the
  module key `k` and current module state `ctx`.

  Default: Returns `ctx` unchanged."
  {:arglists '[[k ctx]]}
  (fn [k _] k))

(defmethod start :default [_ ctx] ctx)
