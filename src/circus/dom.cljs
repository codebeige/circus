(ns circus.dom
  (:require [clojure.string :as str]))

(defn render!
  "Updates dom node `n` to match hiccup form `h`.

  `h` must be fully normalized. Mutates and returns `n`."

  [n [tag attrs & _body]]

  (let [tag-name (str/lower-case (.-tagName n))]
    (assert (= tag-name (str/lower-case (name tag)))
            (str "Tag names do not match: expected "
                 (keyword tag-name)
                 ", but got "
                 tag)))

  (doseq [[k v] attrs]
    (.setAttribute n (name k) v))
  (doseq [k (map #(keyword (.-name %)) (.-attributes n))]
    (when-not (contains? attrs k)
      (.removeAttribute n (name k))))
  n)
