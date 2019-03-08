(ns apptoolkit.domain-model-editor.components.projection-page
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]

   [material-desktop.api :refer [<subscribe dispatch>]]
   [material-desktop.components :as mdc]))


(defn EventHandlerCard
  [event-handler]
  (let [event (:event event-handler)]
    [:> mui/Card
     [:> mui/CardContent
      [mdc/Double-H2 (:ident event) "Event"]
      [mdc/Data event-handler]]]))


(defn ProjectionCard [projection]
  [:> mui/Card
   [:> mui/CardContent
    [mdc/Double-H2 (:ident projection) "Projection"]
    [mdc/Data projection]]])


(defn ProjectionWorkarea
  [args]
  (let [module-ident (-> args (get "module") keyword)
        projection-id (get args "projection")
        projection (<subscribe [:domain-model-editor/projection
                                {:module-ident module-ident
                                 :projection-id projection-id}])]
    [:div
     ;; (mdc/Data args)
     ;; [:hr]
     ;; (mdc/Data projection)
     ;; [:hr]
     [mdc/Row
      {}
      (into [mdc/Column
             {}]
            (mapv (fn [event-handler]
                    [EventHandlerCard event-handler])
                  (get projection :event-handlers)))
      [ProjectionCard projection]]]))
