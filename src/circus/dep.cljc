(ns circus.dep
  (:require [circus.module :as module])
  (:refer-clojure :exclude [resolve])
  #?(:clj (:import [clojure.lang IDeref IRecord])))

(defn- when-resolved [x]
  (when (not= ::unresolved x) x))

(defrecord Dep [key val]
  IDeref
  #?(:clj (deref [_]
            (when-resolved val))
     :cljs (-deref [_]
             (when-resolved val))))

#?(:clj (defmethod print-method Dep [x ^java.io.Writer w]
          (let [p (get-method print-method IRecord)]
            (p x w))))

(defn make [k]
  (->Dep k ::unresolved))

(defn resolve
  "Resolves `dep` in context of `system`.
  Returns a new `dep` with `val` set to the export from the module referrred by
  `dep`."
  {:arglists '[[dep system]]}
  [{:keys [key] :as dep} system]
  (assoc dep :val (module/export key (get system key))))

(defn dep?
  "Returns `true` if `x` is a dependency."
  [x]
  (instance? Dep x))

(defn resolved?
  "Returns `true` if `dep` has a value that can be obtained with [[deref]]."
  [dep]
  (assert (dep? dep))
  (not= ::unresolved (:val dep)))

(defn deps [f]
  (distinct (keep #(when (dep? %) (:key %)) (tree-seq coll? seq f))))

(defn deps? [f]
  (seq (deps f)))

(defn ex-cyclic-dep
  "Create an instance of `ExceptionInfo` specific to cyclic dependency errors.

  `target` is the module key that triggered the exception. `visited` is a set
  of keys for the previously walked modules."
  [k visited]
  (ex-info "Cyclic dependency detected"
           {:type ::cyclic-dep
            :target k
            :cycle visited}))

(defn ex-cyclic-dep?
  "Return true if `ex` was created by [[ex-cyclic-dep]]."
  [ex]
  (= ::cyclic-dep (:type (ex-data ex))))

(defn topo-seq
  "Returns a sequence of module keys `ks` and their dependencies in topological
  order.

  Each module key is only present once. Dependencies are guaranteed to be
  ordered before modules that depend on them. Throws [[ex-cyclic-dep]] when a
  circular dependency is detected."
  ([system] (topo-seq system (keys system)))
  ([system ks]
   (let [postwalk (fn postwalk [k visited]
                    (when (k visited)
                      (throw (ex-cyclic-dep k visited)))
                    (lazy-seq
                     (concat
                      (mapcat #(postwalk % (conj visited k))
                              (deps (get system k)))
                      [k])))]
     (distinct (mapcat postwalk ks (repeat #{}))))))


(comment
 (let [system {:A {:b (make :B)}
               :B {:c (make :C)
                   :d (make :D)}
               :E {:b (make :B)
                   :f (make :F)
                   :g (make :G)}}]

   (topo-seq system [:A :B :E]))

 (concat (mapcat vector nil) [1 2 3]))
