(ns apptoolkit.devtools.headsup.models
  (:require
   [browser-headsup.api :as headsup]
   [browser-headsup.ui :as headsup-ui :refer [Data]]
   [material-desktop.api :refer [<subscribe]]))


(defn Model
  [{:as model :keys [name doc]}]
  [:div
   [:h3 (str name)]
   (if doc
     [:p
      {:style {:color headsup-ui/col-dimmed}}
      doc])
   [:div
    [Data (dissoc model :name :doc)]]])


(defn Models
  [type models]
  [:div
   [:h2 (str type)]
   (into [:div]
         (mapv Model models))])


(defn ModelsTypes []
  (let [models-by-type (<subscribe [:app/models])]
    (into [:div]
          (mapv #(Models (first %) (vals (second %)))
                models-by-type))))


(defn Root []
  [:div
   [ModelsTypes]])


(headsup/def-tab ::models "Models" [Root])
