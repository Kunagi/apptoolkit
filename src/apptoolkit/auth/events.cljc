(ns apptoolkit.auth.events
  (:require
   [appkernel.api :as app]))


(app/def-event :auth/user-signed-in-with-oauth
  :doc "A known user signed in by OAuth."
  :args [{:name   :user
          :type   :ref
          :entity :user}
         {:oauth  :map}])
