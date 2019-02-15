(ns apptoolkit.user.mod
  (:require
   [appkernel.api :as app]

   [apptoolkit.user.googleinfos-projector]))


(app/def-event :user/google-userinfo-received
  :durable? true)
