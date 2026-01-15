(ns seven-guis.ui.widgets)

(defn button [props children]
  [:button
   props
   children])

(defn window [{:keys [title]} & body]
  [:div.ui-widgets-window
   [:div.header
    [:div.traffic-lights [:span]]
    [:h4 title]]
   [:div.main body]])
