(ns apptoolkit.devtools.headsup.db
  (:require
   [browser-headsup.api :as headsup]
   [browser-headsup.ui :refer [Data]]
   [material-desktop.api :refer [<subscribe]]))


(defn Root []
  [:div
   [Data (<subscribe [:app/db])]])


(headsup/def-tab ::db "db" [Root])
