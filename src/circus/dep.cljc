(ns circus.dep
  (:require [circus.module :as module]
            #?(:clj [clojure.core :refer [print-method]]))
  (:refer-clojure :exclude [key resolve])
  #?(:clj (:import [clojure.lang IDeref])))

(defprotocol Resolvable
  (key [this]
    "Returns the key for resolving `dep` in context of the current system.")
  (resolve [this m]
    "Resolves `this` in context of system `m`.
    Returns a new `dep` that can be dereferenced to retrieve the exported value
    from the current module state in `m`."))

(declare ->Dep)

(deftype Dep [k m]
  IDeref
  #?(:clj (deref [_] (module/export k (get m k)))
     :cljs (-deref [_] (module/export k (get m k))))
  Resolvable
  (key [_] k)
  (resolve [_ m] (->Dep k m)))

#?(:clj
   (defmethod print-method Dep [this writer]
     (.write writer "#circus/dep[")
     (print-method (key this) writer)
     (.write writer " ")
     (print-method @this writer)
     (.write writer "]"))
   :cljs
   (extend-type Dep
     IPrintWithWriter
     (-pr-writer [this writer opts]
       (-write writer "#circus/dep[")
       (-pr-writer (key this) writer opts)
       (-write writer " ")
       (if (satisfies? IPrintWithWriter @this)
         (-pr-writer @this writer opts)
         (-write writer (pr-str @this)))
       (-write writer "]"))))

(defn make [k]
  (->Dep k nil))

(defn dep?
  "Returns `true` if `x` is a dependency."
  [x]
  (instance? Dep x))

(defn deps [f]
  (distinct (keep #(when (dep? %) (key %)) (tree-seq coll? seq f))))

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
