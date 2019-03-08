(ns apptoolkit.domain-model-editor.components.breadcrumbs
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]

   [material-desktop.api :refer [dispatch>]]
   [material-desktop.components :as mdc]
   [material-desktop.desktop.navigation :as navigation]))


(defn EventBreadcrumb [text event]
  [:> mui/Link
   {:style {:font-size "80%"
            :cursor :ponter}
    :on-click #(dispatch> event)
    :href "#"}
   text])


(defn LinkBreadcrumb [text href active?]
  [:> mui/Link
   {:style {:font-size "80%"
            :cursor :ponter
            :font-weight (if active? 500)}
    :href href}
   text])


(defn PageBreadcrumb [text page-key page-args active?]
  [LinkBreadcrumb
   text
   (navigation/construct-page-path page-key page-args)
   active?])


(defn Breadcrumbs [options & breadcrumbs]
  [mdc/ErrorBoundary
   [:div.Breadcrumbs
    {:style {:display :flex
             :margin-bottom (mdc/spacing 1)}}
    [:> mui/Card
     [:> mui/CardContent
      {:style {:padding (mdc/spacing 1)}}
      (into [:div
             {:style {:display :flex
                      :align-items :center}}
             [:> icons/ArrowRight
              {:style {:color (-> mdc/palette :greyed)
                       :height "22px"}}]]
            (interpose
             [:> icons/ArrowRight
              {:style {:color (-> mdc/palette :greyed)
                       :height "22px"}}]
             breadcrumbs))]]]])

;;;

(defn ModelBreadcrumb [active?]
  [PageBreadcrumb "Domain Model" :domain-model-editor/model {} active?])


(defn ModuleBreadcrumb [module active?]
  [PageBreadcrumb
   (str "Module: " (name (:ident module)))
   :domain-model-editor/module
   {"module" (name (:ident module))}
   active?])


(defn ElementBreadcrumb [element active?]
  (let [module (:module element)
        type (:db/type element)
        type-text (name type)
        page-key (keyword (name :domain-model-editor) (name type))]
    [PageBreadcrumb
     (str type-text ": " (name (:ident element)))
     page-key
     {"module" (name (:ident module))
      (name type) (:db/id element)}
     active?]))


(defn BreadcrumbsForModel
  [module]
  [Breadcrumbs {}
   [ModelBreadcrumb true]])


(defn BreadcrumbsForModule
  [module]
  [Breadcrumbs {}
   [ModelBreadcrumb false]
   [ModuleBreadcrumb module true]])


(defn BreadcrumbsForElement
  [element]
  [Breadcrumbs {}
   [ModelBreadcrumb false]
   [ModuleBreadcrumb (:module element) false]
   [ElementBreadcrumb element true]])
