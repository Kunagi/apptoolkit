(ns apptoolkit.browserapp.api
  (:require
   [cljs.reader :as reader]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [re-frame.db :as rf-db]

   [model-driver.model.api]

   [material-desktop.desktop.api :as desktop]
   [material-desktop.app :as desktop-app]
   [appkernel.integration :as integration]
   ;; [appkernel.eventhandling :as eventhandling]
   [appkernel.transacting :as transacting]
   [appkernel.api :as app]

   [apptoolkit.browserapp.subscriptions]))




(defn- integrate-event-handler-with-re-frame
  [event-key]
  (tap> [::integrate-event-handler-with-re-frame event-key])
  (rf/reg-event-db
   event-key
   (fn [db [event-name event-args]]
     (let [event (-> event-args
                     (or {})
                     (assoc :app/event event-name))]
       (-> db
           (transacting/transact event)
           :db)))))


(defn integrate-event-handlers-with-re-frame
  []
  (tap> [::integrate-event-handlers-with-re-frame])
  (doall (map integrate-event-handler-with-re-frame
              (-> @rf-db/app-db
                  (get-in [:appkernel/event-handlers])
                  (vals)
                  (->> (map :event) (into #{}))))))


(defn- integrate-command-handler-with-re-frame
  [command-key]
  (tap> [::integrate-command-handler-with-re-frame command-key])
  (rf/reg-event-db
   command-key
   (fn [db [command-name command-args]]
     (let [command (-> command-args
                       (or {})
                       (assoc :app/command command-name))]
       (-> db
           (transacting/transact command)
           :db)))))


(defn integrate-command-handlers-with-re-frame
  []
  (tap> [::integrate-command-handlers-with-re-frame])
  (doall (map integrate-command-handler-with-re-frame
              (-> @rf-db/app-db
                  (get-in [:appkernel/command-handlers])
                  (vals)
                  (->> (map :command) (into #{}))))))




(defn mount-app []
  (desktop-app/mount-app))


(defn start [config-edn ui-root-component config]
  (tap> [::start config-edn])
  (desktop-app/start ui-root-component)
  (rf/dispatch-sync [::init])
  (integrate-event-handlers-with-re-frame)
  (integrate-command-handlers-with-re-frame)
  ;; (rf/dispatch-sync [(keyword (:app/name config-edn) "init")])
  (app/start! (merge
               (or (reader/read-string config-edn) {})
               config))
  (mount-app))


(rf/reg-event-db
 ::init
 (fn [db _]
   (-> db
       (merge (integration/integrate!
               {:db-f (fn [] @rf-db/app-db)
                :update-db-f (fn [f] (swap! rf-db/app-db f))
                :dispatch-f (fn [event] (rf/dispatch [(:app/event event) event]))})))))
