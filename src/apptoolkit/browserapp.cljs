
(ns apptoolkit.browserapp
  (:require
   [appkernel.api]))

(.log js/console "loading browserapp")

(defn -main []
  (.log js/console "main"))
