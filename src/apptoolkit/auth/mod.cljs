(ns apptoolkit.auth.mod
  (:require
   [re-frame.core :as rf]
   [apptoolkit.auth.events]))

(rf/reg-sub
 :auth/user-id
 (fn [db _]
   (get db :auth/user-id)))
