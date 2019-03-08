(ns apptoolkit.domain-model-editor.components.model-page
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]
   [re-frame.core :as rf]

   [facts-db.api :as db]
   [material-desktop.desktop.api :as desktop]
   [material-desktop.api :refer [<subscribe dispatch>]]
   [material-desktop.components :as mdc]
   [material-desktop.toolbar :as toolbar]
   [material-desktop.expansion-panel-list :as expansion-panel-list]

   [apptoolkit.domain-model-editor.components.breadcrumbs :as breadcrumbs]))


(defn ModuleCard
  [{:keys [ident goto-event]}]
  [:div
   {:style {:margin-top (mdc/spacing 1)}}
   [:> mui/Card
    {:on-click #(dispatch> goto-event)
     :style {:cursor :pointer}}
    ;;:min-width "200px"}}
    [:> mui/CardContent
     [:span
      {:style {:font-weight 600}}
      (str (name ident))]]]])


(defn ModelWorkarea
  []
  (let [model (<subscribe [:domain-model-editor/model])]
    [:div
     [breadcrumbs/BreadcrumbsForModel]
     ;; [:hr]
     ;; (mdc/Data (<subscribe :domain-model/modules-ids {:model-id model-id}))
     ;; (mdc/Data model)
     ;; [:hr]
     (into [:div]
           (mapv (fn [module]
                   [ModuleCard module])
                 (-> model :modules)))]))


;;; re-frame events



