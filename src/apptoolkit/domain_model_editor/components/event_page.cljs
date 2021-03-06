(ns apptoolkit.domain-model-editor.components.event-page
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]

   [material-desktop.api :refer [<subscribe dispatch>]]
   [material-desktop.components :as mdc]
   [apptoolkit.domain-model-editor.components.breadcrumbs :as breadcrumbs]))


(defn AttributeCard [attribute]
  [:> mui/Card
   [:> mui/CardContent
    [mdc/Double-H2 (:ident attribute) "Attribute"]
    [mdc/Data attribute]]])


(defn ProjectionHandlerCard
  [handler]
  (let [projection (:projection handler)]
    [:> mui/Card
     [:> mui/CardContent
      [mdc/Double-H2 (:ident projection) "Projection"]
      [mdc/Data handler]]]))


(defn EventCard [event]
  [:> mui/Card
   [:> mui/CardContent
    [mdc/Double-H2 (:ident event) "Event"]
    (into [:div]
          (mapv (fn [attribute]
                  [AttributeCard attribute])
                (get event :attributes)))
    [:hr]
    [mdc/Data event]]])


(defn EventWorkarea
  [args]
  (let [module-ident (-> args (get "module") keyword)
        event-id (get args "event")
        event (<subscribe [:domain-model-editor/event
                           {:module-ident module-ident
                            :event-id event-id}])]
    [:div
     [breadcrumbs/BreadcrumbsForElement event]
     ;; (mdc/Data args)
     ;; [:hr]
     ;; (mdc/Data event)
     ;; [:hr]
     [mdc/Row
      {}
      [EventCard event]
      (into [mdc/Column
             {}]
            (mapv (fn [handler]
                    [ProjectionHandlerCard handler])
                  (get event :projection-handlers)))]]))
