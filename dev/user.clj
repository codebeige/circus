(ns user
  #_{:clj-kondo/ignore [:refer-all]}
  (:require [clojure.pprint :as pprint]
            [clojure.repl :refer :all]))

(prefer-method pprint/simple-dispatch
               clojure.lang.IPersistentMap
               clojure.lang.IDeref)
