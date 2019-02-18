(ns apptoolkit.browserapp.api
  (:require
   [cljs.reader :as reader]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [re-frame.db :as rf-db]

   [appkernel.integration :as integration]
   ;; [appkernel.eventhandling :as eventhandling]
   [appkernel.transacting :as transacting]
   [appkernel.api :as app]

   [material-desktop.init :as init]
   [material-desktop.components :as mdc]
   [material-desktop.desktop :as desktop]

   [apptoolkit.browserapp.subscriptions]))


(defn subscribe
  ([subscription-name]
   (subscribe subscription-name {}))
  ([subscription-name subscription-args]
   (if-not (qualified-keyword? subscription-name)
     (throw (ex-info "Subscription name needs to be a qualified keyword."
                     {:subscription-name subscription-name
                      :subscription-args subscription-args})))
   (if-not (or (nil? subscription-args) (map? subscription-args))
     (throw (ex-info "Subscription args need to be a map."
                     {:subscription-name subscription-name
                      :subscription-args subscription-args})))
   (rf/subscribe [subscription-name subscription-args])))


(defn <subscribe
  ([subscription-name]
   (<subscribe subscription-name {}))
  ([subscription-name subscription-args]
   (if-let [signal (subscribe subscription-name subscription-args)]
     @signal)))


(defn- integrate-event-handler-with-re-frame
  [event-key]
  (tap> [::integrate-event-handler-with-re-frame event-key])
  (rf/reg-event-db
   event-key
   (fn [db [event-name event-args]]
     (let [event (-> event-args
                     (or {})
                     (assoc :app/event event-name))]
       (transacting/transact db event)))))


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
       (transacting/transact db command)))))


(defn integrate-command-handlers-with-re-frame
  []
  (tap> [::integrate-command-handlers-with-re-frame])
  (doall (map integrate-command-handler-with-re-frame
              (-> @rf-db/app-db
                  (get-in [:appkernel/command-handlers])
                  (vals)
                  (->> (map :command) (into #{}))))))


(defonce !ui-root-component (atom [:div "ui-root-component"]))


(defn mount-app []
  (r/render
   [mdc/ErrorBoundary
    @!ui-root-component]
   (js/document.getElementById "app")))


(defn start [config-edn ui-root-component]
  (tap> [::start config-edn])
  (reset! !ui-root-component ui-root-component)
  (init/install-roboto-css)
  (rf/dispatch-sync [::init ui-root-component])
  (integrate-event-handlers-with-re-frame)
  (integrate-command-handlers-with-re-frame)
  (app/start! (if config-edn
                (reader/read-string config-edn)
                {}))
  (mount-app))


(rf/reg-event-db
 ::init
 (fn [db _]
   (-> db
       (merge (integration/integrate!
               {:db-f (fn [] @rf-db/app-db)
                :update-db-f (fn [f] (swap! rf-db/app-db f))
                :dispatch-f (fn [event] (rf/dispatch [(:app/event event) event]))})))))
