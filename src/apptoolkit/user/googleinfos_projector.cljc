(ns apptoolkit.user.googleinfos-projector
  (:require
   [appkernel.api :as app]
   [facts-db.api :as db]))


(defn on-google-userinfo-received
  [db event]
  (tap> [::item-created db event])
  (let [userinfo (:userinfo event)
        sub (:sub userinfo)
        userinfo (assoc userinfo :db/id sub)]
    ;; FIXME (db/++ db userinfo)))
    db))


(app/def-projector :user/googleinfos

  :durable? true

  :event-handlers
  [{:event :user/google-userinfo-received
    :db-f on-google-userinfo-received}])
