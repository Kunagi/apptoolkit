(ns apptoolkit.http-server.oauth
  (:require
   [clj-http.client :as http]

   [appkernel.api :as app]

   [apptoolkit.secrets :as secrets]
   [apptoolkit.user.mod]
   [apptoolkit.auth.mod :as auth]))


(defn create-base-config
  [provider-key provider-specific-config]
  (let [own-uri (get-in (app/db) [:http-server/uri])
        prefix (or own-uri "")
        secrets (get-in secrets/secrets [:oauth provider-key])]
    (if-not secrets
      nil
      (-> {:launch-uri       (str "/oauth/" (name provider-key))
           :redirect-uri     (str prefix "/oauth/" (name provider-key) "/callback")
           :landing-uri      (str "/oauth/completed")
           :basic-auth?      true}
          (merge provider-specific-config)
          (merge secrets)))))


(defn google-base-config []
  (create-base-config
   :google
   {:authorize-uri    "https://accounts.google.com/o/oauth2/v2/auth"
    :access-token-uri "https://www.googleapis.com/oauth2/v4/token"
    :scopes           ["openid" "email" "profile"]}))


(defn create-google-config
  [db]
  (if-let [app-config (get-in db [:oauth :google])]
    (-> (google-base-config)
        (merge app-config))))


(defn create-ring-oauth2-config
  [db]
  {:google (create-google-config db)})


;; (defn load-google-email
;;   [token]
;;   (-> "https://www.googleapis.com/oauth2/v3/userinfo"
;;       (http/post {:accept :json
;;                   :as :json
;;                   :headers {"Authorization" (str "Bearer " token)}})
;;       :body
;;       :email))


(defn decode-jwt
  [token]
  (let [decoder (com.auth0.jwt.JWT/decode token)
        claims (.getClaims decoder)
        keys (.keySet claims)]
    (reduce
     (fn [ret key]
       (let [claim (.get claims key)]
         (if (.isNull claim)
           ret
           (assoc ret (keyword key) (or (.asString claim)
                                        (.asInt claim)
                                        (boolean (.asBoolean claim)))))))
     {}
     keys)))


(defn handle-oauth-completed-
  [request]
  (let [access-tokens (-> request :session :ring.middleware.oauth2/access-tokens)
        google (:google access-tokens)
        access-token (:token google)
        id-token (:id-token google)
        userinfo (decode-jwt id-token)]
    (app/dispatch {:app/event :user/google-userinfo-received
                   :userinfo userinfo})
    (if-let [user-id (auth/authenticate {:oauth {:service :google
                                                 :sub (:sub userinfo)
                                                 :email (:email userinfo)
                                                 :name (:name userinfo)}})]
      {:session {:auth/user-id user-id}
       :status 303
       :headers {"Location" "/"}}
      {:status 403})))



(defn handle-oauth-completed
  [request]
  (handle-oauth-completed- request))
