(ns apptoolkit.secrets
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]

   [appkernel.paths :as paths]))


(defn load-secrets []
  (let [file (io/as-file (str (paths/configs-dir) "/secrets.edn"))]
    (if-not (.exists file)
      nil
      (try
        (edn/read-string (slurp file))
        (catch Exception ex
          (throw (ex-info (str "Failed to read file " (.getAbsolutePath file))
                          {:file file})))))))


(def secrets (load-secrets))
