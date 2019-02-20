(ns apptoolkit.auth.mod
  (:require
   [compojure.core :as compojure]

   [appkernel.api :as app]

   [apptoolkit.auth.events]
   [apptoolkit.auth.projectors.users-by-oauth]))


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
    (app/dispatch {:app/command :app/passthrough-events
                   :events [{:app/event :user/user-signed-up
                             :user user}
                            {:app/event :auth/user-signed-in-with-oauth
                             :user user-id
                             :oauth {:service service
                                     :sub sub
                                     :email email}}]})
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


(app/def-query-responder ::sign-out-route
  :query :http-server/routes
  :f (fn [db args]
       [(compojure/GET "/sign-out" [] logout-handler)]))
