(ns apptoolkit.browserapp.api
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [re-frame.db :as rf-db]

   [appkernel.lifecycle :as lifecycle]
   [appkernel.integration :as integration]
   [appkernel.eventhandling :as eventhandling]

   [material-desktop.init :as init]
   [material-desktop.components :as mdc]
   [material-desktop.desktop :as desktop]))


(defn- integrate-event-handler-with-re-frame
  [event-key]
  (rf/reg-event-db
   event-key
   (fn [db event]
     (eventhandling/handle-event db event))))


(defn- integrate-event-handlers-with-re-frame
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
  (rf/dispatch-sync [:appkernel/initialized])
  (mount-app))


(defn on-figwheel-after-load []
  ;;(app/log :info ::figwheel-after-load)
  (integrate-event-handlers-with-re-frame)
  (mount-app))


(rf/reg-event-db
 ::init
 (fn [db _]
   (merge db (integration/integrate!
               (fn [f] (swap! rf-db/app-db f))
               (fn [] @rf-db/app-db)))))


(rf/reg-sub
 :db
 (fn [db _]
   db))
