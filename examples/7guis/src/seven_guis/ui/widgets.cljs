(ns seven-guis.ui.widgets)

(defn window [{:keys [title]} & body]
  [:div.ui-widgets-window
   [:div.header
    [:div.traffic-lights [:span]]
    [:h4 title]]
   [:div.main body]])
