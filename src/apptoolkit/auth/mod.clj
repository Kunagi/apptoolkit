(ns apptoolkit.auth.mod
  (:require
   [compojure.core :as compojure]

   [appkernel.api :as app]

   [apptoolkit.auth.user-ids-by-oauth-projector]))


(defn fail
  [message]
  (tap> [::authentication-failed message])
  (throw (ex-info (str "Authentication failed. " message)
                  {})))


(defn success
  [user-id]
  user-id)


(defn create-user-by-oauth
  [{:as oauth :keys [service sub email name]}]
  (let [user-id (app/new-uuid)
        user {:db/id user-id
              :email email
              :name name}]
    (app/dispatch {:app/event :auth/oauth-for-user-connected
                   :user-id user-id
                   :oauth {:service service
                           :sub sub}})
    user-id))


(defn authenticate-by-oauth
  [{:as oauth :keys [service sub email]}]
  (if-not sub (fail (str "Missing :oauth ::sub.")))
  (if-not service (fail (str "Missing :oauth ::service.")))
  (if-let [user-id (app/q-1! [:auth/user-id-by-oauth {:oauth oauth}])]
    (success user-id)
    (if-let [user-id (if email (app/q-1! [:user/user-id-by-email {:email email}]))]
      (success user-id)
      (create-user-by-oauth oauth))))


(defn authenticate
  [{:as authentication-data :keys [oauth]}]
  (cond
    oauth (authenticate-by-oauth oauth)
    :else (fail "Unsupported authentication-data.")))


(defn logout-handler [request]
  {:session nil
   :status 303
   :headers {"Location" "/"}})


(app/def-query-responder ::logout-route
  :query :http-server/routes
  :f (fn [db args]
       [(compojure/GET "/logout" [] logout-handler)]))
