(ns apptoolkit.auth.user-ids-by-oauth-projector
  (:require
   [appkernel.api :as app]
   [facts-db.api :as db]))


(defn on-oauth-for-user-connected
  [db event]
  db)


(app/def-projector :auth/user-ids-by-oauth

  :durable? true

  :event-handlers
  [{:event :auth/oauth-for-user-connected
    :db-f on-oauth-for-user-connected}])




;; TODO move to projector - with auto-registration by registry

(app/def-query-responder :auth/user-id-by-oauth
  :query :auth/user-id-by-oauth
  :f (fn [db query]
       []))


;; TODO move to events

(app/def-event :auth/oauth-for-user-connected)
