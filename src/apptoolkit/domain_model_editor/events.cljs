(ns apptoolkit.domain-model-editor.events
  (:require
   [re-frame.core :as rf]
   [facts-db.api :as db]
   [facts-db.ddapi :as ddapi]
   [material-desktop.desktop.api :as desktop]
   [material-desktop.desktop.navigation :as navigation]))


(rf/reg-event-db
 :domain-model-editor/create-entity-triggered
 (fn [db [_ {:keys [module-id container-id]}]]
   (-> db
       (desktop/open-form-dialog
        :title "Create new Entity"
        :form {:fields [{:key :ident
                         :label "Identifier"
                         :auto-focus true}]}
        :submit-event [:domain-model-editor/create-entity-submitted
                       {:module-id module-id
                        :container-id container-id}]))))


(rf/reg-event-db
 :domain-model-editor/create-entity-submitted
 (fn [db [_ {:keys [values module-id container-id]}]]
   (let [ident (:ident values)
         entity-id (db/new-uuid)]
     (-> db
         (update-in [:ddapi/dbs :domain-model :singleton]
                    ddapi/events> [[:entity-created
                                    {:id entity-id
                                     :ident ident
                                     :module-id module-id
                                     :container-id container-id}]])
         (navigation/navigate!-with-db :domain-model-editor/entity
                                       {:entity-id entity-id})))))


(rf/reg-event-db
 :domain-model-editor/edit-element-fact-triggered
 (fn [db [_ {:keys [element-id fact]}]]
   (let [domain-model-db (get-in db [:ddapi/dbs :domain-model :singleton])
         value (db/fact domain-model-db element-id fact)]
     (-> db
         (desktop/open-form-dialog
          :title "Edit fact"
          :form {:fields [{:key fact
                           :label (str fact)
                           :default-value value
                           :auto-focus true}]}
          :submit-event [:domain-model-editor/element-fact-submitted
                         {:element-id element-id
                          :fact fact}])))))


(rf/reg-event-db
 :domain-model-editor/element-fact-submitted
 (fn [db [_ {:keys [values element-id fact]}]]
   (let [value (get values fact)]
     (-> db
         (update-in [:ddapi/dbs :domain-model :singleton]
                    ddapi/events> [[:element-fact-updated
                                    {:element-id element-id
                                     :fact fact
                                     :value value}]])))))
