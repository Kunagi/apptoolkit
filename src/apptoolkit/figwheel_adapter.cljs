(ns ^:figwheel-hooks apptoolkit.figwheel-adapter
  (:require
   [appkernel.load-this-namespace-to-activate-dev-mode]
   [apptoolkit.browserapp.api :as browserapp]))

(defn  ^:after-load on-figwheel-after-load []
  (tap> ::on-figwheel-after-load)
  (browserapp/integrate-event-handlers-with-re-frame)
  (browserapp/mount-app))
