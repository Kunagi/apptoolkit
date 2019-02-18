(ns apptoolkit.secrets
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]

   [appkernel.paths :as paths]))


(defn load-secrets [db]
  (let [dir (paths/config-dir (:app/name db))
        file (io/as-file (str dir "/secrets.edn"))]
    (if-not (.exists file)
      nil
      (try
        (edn/read-string (slurp file))
        (catch Exception ex
          (throw (ex-info (str "Failed to read file " (.getAbsolutePath file))
                          {:file file})))))))


