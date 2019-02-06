(ns apptoolkit.google.maps
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]

   [appkernel.api :as app]
   [material-desktop.components :as mdc]))


(defn ^:export googleMapsCallback []
  (rf/dispatch [::google-js-loaded]))


(defn google-maps-js-url [google-api-key]
  (str "https://maps.googleapis.com/maps/api/js?key="
       google-api-key
       "&libraries=places"
       "&callback=apptoolkit.google.maps.googleMapsCallback"))


(defn install-google-maps-js! [google-api-key]
  (let [script-tag (-> (.createElement js/document "script"))
        head-tag (.-head js/document)]
    (set! (.-src script-tag) (google-maps-js-url google-api-key))
    (.appendChild head-tag script-tag)))


(defn initialize-map [id]
  (let [map-tag (.getElementById js/document id)
        config {:center {:lat -34.397
                         :lng 150.644}
                :zoom 8}
        map (js/google.maps.Map. map-tag (clj->js config))
        places-service (js/google.maps.places.PlacesService. map)]
    (rf/dispatch [::map-initialized {:js-loaded? true
                                     :initialized? true
                                     :map map
                                     :places-service places-service}])))


(defn GoogleMap [id]
  (reagent/create-class
   {:display-name  "GoogleMap"
    :reagent-render
    (fn [id]
      [:div {:id id :style {:height "100%"}}])
    :component-did-mount
    (fn [this]
      (initialize-map id))}))


(defn Map []
  (let [google-js-loaded? @(rf/subscribe [::google-js-loaded?])]
    [:div
     {:style {:height "400px"
              :border "1px solid #ccc"}}
     (if google-js-loaded?
       [GoogleMap "map"]
       [:div "Loading Google Map..."])]))


;;; subscriptions

(rf/reg-sub
 ::google-js-loaded?
 (fn [db _]
   (get-in db [:app/google-maps :js-loaded?])))


;;; events


(app/def-event-handler ::install-google-maps-script
  :event :app/started
  :f (fn [db event]
       (install-google-maps-js! (app/q-1 db [:app/google-api-key]))
       db))


(rf/reg-event-db
 ::google-js-loaded
 (fn [db _]
   (tap> ::google-js-loaded)
   (assoc db :app/google-maps {:js-loaded? true})))


(rf/reg-event-db
 ::map-initialized
 (fn [db [_ refs-map]]
   (tap> ::map-initialized)
   (assoc db :app/google-maps refs-map)))
