(ns apptoolkit.domain-model-editor.components.entity-page
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]

   [material-desktop.api :refer [<subscribe dispatch>]]
   [material-desktop.components :as mdc]
   [material-desktop.fieldset :as fieldset]

   [apptoolkit.domain-model-editor.components.breadcrumbs :as breadcrumbs]))


(defn EntityCard [entity]
  [:> mui/Card
   [:> mui/CardContent
    [:h4 (-> entity :ident name)]
    [mdc/Data entity]
    [:hr]
    [fieldset/Fieldset
     :rows [
            {:fields [{:label "Identifier"
                       :value (-> entity :ident)
                       :on-click #(dispatch> [:domain-model-editor/edit-element-fact-triggered
                                              {:element-id (:db/id entity)
                                               :fact :ident}])}]}
            {:fields [{:label "Container"
                       :value (-> entity :container)}]}
            {:fields [{:label "Components"
                       :value (-> entity :components)}]}]]]])

(defn EntityWorkarea
  [args]
  (let [module-ident (-> args (get "module") keyword)
        entity-id (get args "entity")
        entity (<subscribe [:domain-model-editor/entity
                            {:module-ident module-ident
                             :entity-id entity-id}])]
    [:div
     ;; (mdc/Data args)
     ;; [:hr]
     ;; (mdc/Data projection)
     ;; [:hr]
     [:div
      [breadcrumbs/BreadcrumbsForElement entity]
      [EntityCard entity]]]))
