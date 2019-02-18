(ns apptoolkit.auth.ui
  (:require
   [apptoolkit.browserapp.api :refer [<subscribe]]
   [material-desktop.components :as mdc]))

(defn SignInCard [& {:as options :keys [text]}]
  [mdc/Card
   {:style {:width "400px"
            :margin "0 auto"}}
   [mdc/CardContent
    (if text
     [:div
      [mdc/Text
       text]
      [:br]])
    [mdc/Button
     :text "Anmeldung mit Google"
     :href "/oauth/google"]]])


(defn SignOutButton []
  [mdc/Button
   :text "Abmelden"
   :href "/sign-out"])

