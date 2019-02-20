(ns apptoolkit.auth.projectors.users-by-oauth
  (:require
   [appkernel.api :as app]
   [facts-db.api :as db]

   [apptoolkit.auth.events]))


(defn on-user-signed-in-with-oauth
  [db {:as event :keys [user oauth]}]
  (let [{:keys [service sub email]} oauth]
    (-> db
        (assoc-in [service :by-sub sub] user)
        (assoc-in [service :by-email email] user))))


(app/def-projector :auth/users-by-oauth
  :doc "Map OAuth identifiers (`sub` and `email`) to users."

  :event-handlers
  [{:event :auth/user-signed-in-with-oauth
    :f on-user-signed-in-with-oauth}])




;; TODO move to projector - with auto-registration by registry

(app/def-query-responder :auth/user-id-by-oauth
  :query :auth/user-id-by-oauth
  :f (fn [db query]
       []))

