(ns circus.hiccup
  (:require [clojure.string :as str]))

(defn hiccup? [x]
  (and (vector? x) (keyword? (first x))))

(def tag-re #"([^#.]+)(?:#([^.]+))?(?:\.(.+))?")

(defn- compact [m]
  (into {} (remove (comp nil? val)) m))

(defn- backfill [m k v]
  (cond-> m
    (some? v) (update k #(or % v))))

(defn- class-prop-seq [x]
  (mapcat #(str/split % #"\s+")
          (when (some? x)
            (cond-> x (string? x) vector))))

(defn- tag-classes-seq [s]
  (when s (str/split s #"\.")))

(defn- normalize-class [props tag-classes]
  (let [class-names (concat (class-prop-seq (:class props))
                            (tag-classes-seq tag-classes))]
    (cond-> props
      (seq class-names) (assoc :class (into #{} class-names)))))

(defn normalize [[tag & body]]
  (let [[_ tag-name tag-id tag-classes] (re-matches tag-re (name tag))
        [props children] (if (map? (first body))
                           [(first body) (rest body)]
                           [{} body])]
    (into
     [(keyword tag-name)
      (-> props
          compact
          (backfill :id tag-id)
          (normalize-class tag-classes))]
     (map #(cond-> % (hiccup? %) normalize))
     children)))

(comment
  (re-find tag-re "div#foo.bar.baz")
  (re-find tag-re "div")
  (re-find tag-re "div.bar.baz")
  (seq nil)
  (str/split "foo  bar    baz" #"\s+")
  (into {} (filter val) {:foo nil :bar "baz"}))

; TODO: spec or malli
; TODO: can I use malli for normalizing tag?
