(ns apptoolkit.user.events
  (:require
   [appkernel.api :as app]))


(app/def-event :user/google-userinfo-received)
