(ns circus.hiccup
  (:require [clojure.string :as str]))

(defn hiccup? [x]
  (and (vector? x)
       (not (map-entry? x))
       (keyword? (first x))))

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

(defn normalize
  "Recursively expand `h` into its canonical form.

  `h` is expected to be a vector with a keyword as the first child, which
  suppoprts a shorthand notation for setting the `:id` and `:class` properties.
  Always adds a properties hash. The `:class` property is converted to a set
  of individual class names.

  Examples:

  ```clj
  => (normalize [:div])
  [:div {}]
  => (normalize [:div#foo.bar \"baz\"])
  [:div {:id \"foo\" :class #{\"bar\"} \"baz\"]
  => (normalize [:div.foo {:class \"bar baz\"]}])
  [:div {:class #{\"foo\" \"bar\" \"baz\"}]
  => (normalize [:div {:class [\"foo bar\" \"baz\"]}])
  [:div {:class #{\"foo\" \"bar\" \"baz\"}]
  ```
  "

  [[tag & body :as h]]

  {:arglists '[[h]]
   :pre [(hiccup? h)]}

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
