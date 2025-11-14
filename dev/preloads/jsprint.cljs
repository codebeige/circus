(ns preloads.jsprint)

(defn pr! [x & xs]
  (apply (.-log js/console) x xs)
  x)

(set! (.-pr js/globalThis) pr!)
