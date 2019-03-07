(ns apptoolkit.domain-model-editor.components.projection-page
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]

   [material-desktop.api :refer [<subscribe dispatch>]]
   [material-desktop.components :as mdc]))


(defn ProjectionWorkarea
  [{:as args :keys [module-ident projection-id]}]
  (let [projection (<subscribe [:domain-model-editor/projection
                                {:module-ident module-ident
                                 :projection-id projection-id}])]
    [:div
     (mdc/Data args)
     [:hr]
     (mdc/Data projection)
     [:hr]]))
