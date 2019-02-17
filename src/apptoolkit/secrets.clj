(ns apptoolkit.secrets
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]

   [appkernel.paths :as paths]))


(defn load-secrets []
  (let [file (io/as-file (str (paths/configs-dir) "/secrets.edn"))]
    (if-not (.exists file)
      nil
      (edn/read-string (slurp file)))))

(def secrets (load-secrets))
