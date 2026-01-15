(ns seven-guis.temp-converter.ui
  (:require [circus.module :as module]
            [seven-guis.ui.widgets :as widgets]))

(defn temp-input [{:keys [label value]}]
  [:div.flex
   {:class "temp-converter-input"}
   [:label
    [:input.mono.text-right.font-xl.bg-white.px-s.border-ui
     {:step 0.1 :type "number" :value (.toFixed value 1)}]
    label]])

(def icon-arrows-right-left
  [:svg {:xmlns "http://www.w3.org/2000/svg"
         :fill "none"
         :viewBox "0 0 24 24"
         :stroke-width "2"
         :stroke "currentColor"
         :style {:width "1.5rem" :height "1.5rem"}}
  [:path {:stroke-linecap "round"
          :stroke-linejoin "round"
          :d "M7.5 21 3 16.5m0 0L7.5 12M3 16.5h13.5m0-13.5L21 7.5m0 0L16.5
             12M21 7.5H7.5"}]])

(defn ui []
  (widgets/window
   {:title "Temperature Converter"}
   [:div.flex.gap-1
    (temp-input {:label "ºC" :value 37.8})
    icon-arrows-right-left
    (temp-input {:label "ºF" :value 100})]))

(defmethod module/start ::ui [_ _]
  {:ui ui})

(defmethod module/export ::ui [_ {:keys [ui]}]
  ui)

(defmethod module/tx ::ui [_ ctx {:keys [type payload]}]
  (when (= ::temp-changed type)
    (prn payload))
  ctx)
