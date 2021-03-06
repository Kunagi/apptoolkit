(ns apptoolkit.domain-model-editor.components.desktop
  (:require
   [re-frame.core :as rf]

   [facts-db.ddapi :as ddapi]
   [material-desktop.api :refer [<subscribe]]
   [material-desktop.components :as mdc]
   [material-desktop.desktop.components.desktop :as desktop]

   [apptoolkit.domain-model-editor.components.model-page :refer [ModelWorkarea]]
   [apptoolkit.domain-model-editor.components.module-page :refer [ModuleWorkarea]]
   [apptoolkit.domain-model-editor.components.entity-page :refer [EntityWorkarea]]
   [apptoolkit.domain-model-editor.components.event-page :refer [EventWorkarea]]
   [apptoolkit.domain-model-editor.components.projection-page :refer [ProjectionWorkarea]]))


(defn create-page [title workarea-component]
  {:appbar {:title [mdc/Double-DIV title "Domain Model Editor"]}
   :workarea {:components [[workarea-component]]}})


(def pages
  {:domain-model-editor/model      (create-page "Domain Model" ModelWorkarea)
   :domain-model-editor/module     (create-page "Module" ModuleWorkarea)
   :domain-model-editor/entity     (create-page "Entity" EntityWorkarea)
   :domain-model-editor/event      (create-page "Event" EventWorkarea)
   :domain-model-editor/projection (create-page "Projection" ProjectionWorkarea)})


(defn Desktop []
  (desktop/PagedDesktop
   {:pages pages
    :home-page :domain-model-editor/model}))
