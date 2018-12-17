(ns  ^:figwheel-hooks apptoolkit.browserapp
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]

   [appkernel.lifecycle :as lifecycle]
   [material-desktop.init :as init]
   [material-desktop.components :as mdc]
   [material-desktop.desktop :as desktop]))


(.log js/console "loading browserapp")


(defn root-ui []
  [mdc/ErrorBoundary
   [desktop/Desktop]])


(defn mount-app []
  (r/render
   [root-ui]
   (js/document.getElementById "app")))


(defn -main []
  (.log js/console "main"))


(defn ^:export start []
  (.log js/console "apptoolkit.browserapp.start()")
  (init/install-roboto-css)
  (mount-app))


(defn ^:after-load on-figwheel-after-load []
  ;;(app/log :info ::figwheel-after-load)
  (mount-app))
