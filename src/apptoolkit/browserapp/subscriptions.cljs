(ns apptoolkit.browserapp.subscriptions
  (:require
   [re-frame.core :as rf]))


(rf/reg-sub
 :app/db
 (fn [db _]
   db))

;;; projections

(rf/reg-sub
 :app/projections
 (fn [db _]
   (get db :appkernel/projections)))


(rf/reg-sub
 :app/projections-by-name
 (fn [_]
   (rf/subscribe [:app/projections]))
 (fn [projections [_ {:keys [name]}]]
   (get projections name)))


(rf/reg-sub
 :app/projection
 (fn [[_ {:keys [name args]}]]
   (rf/subscribe [:app/projections-by-name {:name name}]))
 (fn [projections [_ {:keys [args]}]]
   (let [args (if args args {})]
     (get projections args))))


(rf/reg-sub
 :app/projection-db
 (fn [[_ sub-args]]
   (rf/subscribe [:app/projection sub-args]))
 (fn [projection _]
   (:db projection)))


;;; events

(rf/reg-sub
 :app/models
 (fn [db _]
   (get db :appkernel/models)))
