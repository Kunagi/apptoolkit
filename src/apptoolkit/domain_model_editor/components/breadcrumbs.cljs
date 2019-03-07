(ns apptoolkit.domain-model-editor.components.breadcrumbs
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]

   [material-desktop.api :refer [dispatch>]]
   [material-desktop.components :as mdc]))


(defn EventBreadcrumb [text event]
  [:> mui/Link
   {:style {:font-size "80%"
            :cursor :ponter}
    :on-click #(dispatch> event)
    :href "#"}
   text])


(defn PageBreadcrumb [text page-key page-args]
  [EventBreadcrumb
   text
   [:material-desktop/activate-page
    {:page-key page-key
     :page-args page-args}]])


(defn Breadcrumbs [options & breadcrumbs]
  [:div.Breadcrumbs
   {:style {:display :flex
            :margin-bottom (mdc/spacing 1)}}
   [:> mui/Card
    [:> mui/CardContent
     {:style {:padding (mdc/spacing 1)}}
     (into [:div
            {:style {:display :flex
                     :align-items :center}}]
           (interpose
            [:> icons/ArrowRight
             {:style {:color (-> mdc/palette :greyed)
                      :height "22px"}}]
            breadcrumbs))]]])

;;;

(defn ModelBreadcrumb []
  [PageBreadcrumb "Domain Model" :domain-model-editor/model {}])

(defn ModuleBreadcrumb [module]
  [PageBreadcrumb
   (:ident module)
   :domain-model-editor/module
   {:module-id (:db/id module)}])
