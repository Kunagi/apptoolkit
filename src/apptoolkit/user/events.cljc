(ns apptoolkit.user.events
  (:require
   [appkernel.api :as app]))


(app/def-event :user/user-signed-up
  :doc "A new user has signed up.")


(app/def-event :user/google-userinfo-received
  :doc "Received userinfo from Google. Usualy from OAuth sign in.")
