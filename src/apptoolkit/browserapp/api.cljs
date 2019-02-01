(ns apptoolkit.browserapp.api
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [re-frame.db :as rf-db]

   [appkernel.lifecycle :as lifecycle]
   [appkernel.integration :as integration]
   [appkernel.eventhandling :as eventhandling]
   [appkernel.api :as app]

   [material-desktop.init :as init]
   [material-desktop.components :as mdc]
   [material-desktop.desktop :as desktop]))


(defn- integrate-event-handler-with-re-frame
  [event-key]
  (tap> [::integrate-event-handler-with-re-frame event-key])
  (rf/reg-event-db
   event-key
   (fn [db [event-name event-args]]
     (let [event (-> event-args
                     (or {}) (or event-args {})
                     (assoc :app/event event-name))]
       (eventhandling/handle-event db event)))))


(defn integrate-event-handlers-with-re-frame
  []
  (tap> [::integrate-event-handlers-with-re-frame])
  (doall (map integrate-event-handler-with-re-frame
              (-> @rf-db/app-db
                  (get-in [:appkernel/event-handlers])
                  (vals)
                  (->> (map :event) (into #{}))))))


(defn root-ui []
  [:div
   [mdc/ErrorBoundary
    [desktop/Desktop]]
   [:hr]
   [:h4 "db"]
   [:pre (str @(rf/subscribe [:db]))]])


(defn mount-app []
  (r/render
   [root-ui]
   (js/document.getElementById "app")))


(defn start []
  (tap> ::start)
  (init/install-roboto-css)
  (rf/dispatch-sync [::init])
  (integrate-event-handlers-with-re-frame)
  (app/start!)
  (mount-app))


(rf/reg-event-db
 ::init
 (fn [db _]
   (merge db (integration/integrate!
              {:db-f (fn [f] (swap! rf-db/app-db f))
               :update-db-f (fn [] @rf-db/app-db)
               :dispatch-f (fn [event] (rf/dispatch [(:app/event event) event]))}))))


(rf/reg-sub
 :db
 (fn [db _]
   db))
