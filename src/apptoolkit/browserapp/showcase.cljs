(ns ^:figwheel-hooks apptoolkit.browserapp.showcase
  (:require
   [apptoolkit.browserapp.api :as app]))


(defn ^:export start []
  (app/start))


(defn ^:after-load on-figwheel-after-load []
  (app/on-figwheel-after-load))
