(ns apptoolkit.domain-model-editor.subs
  (:require
   [re-frame.core :as rf]
   [facts-db.api :as db]
   [facts-db.ddapi :as ddapi]))


(rf/reg-sub
 :domain-model/model
 (fn [db _]
   (get db :domain-model/model)))


;; TODO move to domain model
(rf/reg-sub
 :domain-model/modules-idents
 (fn [db _]
   (-> db :domain-model/model :modules keys)))


;; TODO move to domain model
(rf/reg-sub
 :domain-model/module
 (fn [db [_ {:keys [module-ident]}]]
   (-> db :domain-model/model :modules (get module-ident))))




(rf/reg-sub
 :domain-model-editor/model
 (fn []
   (rf/subscribe [:domain-model/modules-idents]))
 (fn [modules-idents _]
   {:modules
    (map (fn [module-ident]
           {:ident module-ident
            :goto-event [:material-desktop/activate-page
                         {:page-key :domain-model-editor/module
                          :page-args {:module-ident module-ident}}]})
         modules-idents)
    :module-subscriptions
    (map (fn [module-id] [:domain-model-editor/module {:module-id module-id}])
         modules-idents)}))



(defn assoc-goto-event-on-element [element]
  (let [module-ident (get-in element [:module :ident])
        type (:db/type element)
        arg-id-key (keyword (str (name type) "-id"))]
    (assoc element :goto-event [:material-desktop/activate-page
                                {:page-key (keyword (name :domain-model-editor)
                                                    (name type))
                                 :page-args {:module-ident module-ident
                                             arg-id-key (:db/id element)}}])))


(rf/reg-sub
 :domain-model-editor/module
 (fn [[_ {:keys [module-ident]}]]
   (rf/subscribe [:domain-model/module {:module-ident module-ident}]))
 (fn [module [_ {:keys [module-ident]}]]
   (let [module-id (db/fact module :module :db/id)]
     (-> module
         (db/tree module-id {:entities {:module {}}
                             :events {:module {}}
                             :projections {:module {}}
                             :types {:module {}}
                             :commands {:module {}}})

         (update :entities    #(mapv assoc-goto-event-on-element %))
         (update :events      #(mapv assoc-goto-event-on-element %))
         (update :projections #(mapv assoc-goto-event-on-element %))
         (update :types       #(mapv assoc-goto-event-on-element %))

         ;; provide events for creating elements
         (assoc :create-events {:entity [:domain-model-editor/create-entity-triggered
                                         {:module-id module-id}]})))))


(rf/reg-sub
 :domain-model-editor/projection
 (fn [[_ {:keys [module-ident]}]]
   (rf/subscribe [:domain-model/module {:module-ident module-ident}]))
 (fn [module [_ {:keys [projection-id]}]]
   (-> module
       (db/tree projection-id
                {:module {}
                 :event-handlers {:event {}}}))))


(rf/reg-sub
 :domain-model-editor/event
 (fn [[_ {:keys [module-ident]}]]
   (rf/subscribe [:domain-model/module {:module-ident module-ident}]))
 (fn [module [_ {:keys [event-id]}]]
   (-> module
       (db/tree event-id
                {:module {}
                 :projection-handlers {:projection {}}
                 :attributes {:type {}
                              :entity {}}}))))


(rf/reg-sub
 :domain-model-editor/entity
 (fn []
   [(rf/subscribe [:domain-model/db])
    (rf/subscribe [:material-desktop/current-page-args])])
 (fn [[domain-model-db current-page-args] _]
   (if-not domain-model-db
     nil
     (let [entity-id (:entity-id current-page-args)]
       (-> domain-model-db
           (db/tree entity-id {:module {}}))))))
