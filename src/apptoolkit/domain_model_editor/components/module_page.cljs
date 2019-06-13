(ns apptoolkit.domain-model-editor.components.module-page
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
   [material-desktop.box-editor :as box-editor]

   [apptoolkit.domain-model-editor.components.breadcrumbs :as breadcrumbs]))


;;; boxes




(defn ElementBox [element]
  [box-editor/Box
   {;;:type-label "Event"
    :label (:ident element)}])


(defn ModuleElementsBox [elements type-label]
  [box-editor/Box
   {:type-label type-label}
   (for [element elements]
     [ElementBox element])])


(defn ModuleBox [module]
  [box-editor/Box
   {:type-label "Module"
    :label (:ident module)}
   ;; (for [d [:a :b :C]]
   ;;   [DummyBox])])
   [ModuleElementsBox (:events module) "events"]
   [ModuleElementsBox (:projections module) "projections"]
   [ModuleElementsBox (:types module) "types"]])


(defn ModuleBoxes [module]
  [:div
   [:hr]
   [box-editor/BoxContainer
    {}
    [ModuleBox module]]
   [:hr]])


;;; generic element components

(defn ElementCard
  [element suffix-component]
  [:div
   {:style {:margin-top (mdc/spacing 1)}}
   [:> mui/Card
    {:on-click #(dispatch> (-> element :goto-event))
     :style {:cursor :pointer}}
    ;;:min-width "200px"}}
    [:> mui/CardContent
     [:span
      {:style {:font-weight 600}}
      (-> element :ident name)]]]
   suffix-component])


(defn ElementsToolbar
  [element-type create-trigger-event]
  [toolbar/PaperToolbar
   {:title (str element-type)}
   [:> mui/IconButton
    {:style {:color :inherit}
     :on-click #(dispatch> create-trigger-event)}
    [:> icons/Add]]])


(defn ElementsList
  [module elements type]
  [:div
   [ElementsToolbar type (-> module :create-events type)]

   (into [:div]
         (mapv (fn [element]
                 [ElementCard element])
               elements))])


;;; custom components for entities

(defn EntityAndComponentsCards
  [entity all-entities-by-id]
  (let [component-entities (map (fn [component-entity-id]
                                  (get all-entities-by-id component-entity-id))
                                (-> entity :components))]
    [ElementCard
     entity
     (into [:div
            {:style {:margin-left (mdc/spacing 2)}}]
           (mapv (fn [component-entity]
                   [EntityAndComponentsCards component-entity all-entities-by-id])
                 component-entities))]))


(defn EntitiesList
  [module]
  [:div
   [ElementsToolbar :entity (-> module :create-events :entity)]

   (let [entities (-> module :entities)
         all-entities-by-id (reduce (fn [m entity]
                                      (assoc m (:db/id entity) entity))
                                    {}
                                    entities)
         root-entities (filter #(not (:container %))
                               entities)]
     (into [:div]
           (mapv (fn [root-entity]
                   [EntityAndComponentsCards root-entity all-entities-by-id])
                 root-entities)))])


;;;


(defn Module
  [module]
  [:div
   [breadcrumbs/BreadcrumbsForModule module]
   [ModuleBoxes module]
   [mdc/Columns
    [EntitiesList module]
    [ElementsList module (-> module :events) :event]
    [ElementsList module (-> module :projections) :projection]
    [ElementsList module (-> module :types) :type]]])


(defn ModuleWorkarea
  [args]
  [:div
   ;; [mdc/Data args]])
   ;; [mdc/Data (<subscribe [:material-desktop/current-page])]
   ;; [:hr]
   ;; [mdc/Data (<subscribe [:domain-model-editor/module {:module-ident :kunagi}])]
   ;; [:hr]
   ;; [mdc/Data page-args]
   ;; [:hr]
   [mdc/SubscriptionProgressBoundary Module [:domain-model-editor/module
                                             {:module-ident (-> args (get "module") keyword)}]]])
