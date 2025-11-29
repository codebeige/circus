(ns circus.module)

(defmulti export
  "Returns external-facing value of module with key `k` derived from its
  current state `ctx`.
  Will be called each time a dependency is resolved. Default: Returns `ctx`
  unchanged."
  (fn [k _ctx] k))

(defmethod export :default [_ ctx] ctx)
