(ns apptoolkit.logging-output
  (:require
   [clojure.term.colors :as c]
   [puget.printer :as puget]
   [appkernel.logging :as logging]))


(defonce lock :lock)

(defn log-record
  [{:as record :keys [source-ns source-name level payload]}]
  (locking lock
    (let [level-bg (case level
                     :err c/on-red
                     :wrn c/on-red
                     :inf c/on-green
                     c/on-blue)]
      (println
       (c/on-grey (c/white (str " " (name level) " ")))
       (level-bg (c/white (c/bold (str " " source-name " "))))
       (c/white source-ns))
      (if payload
        (puget/cprint payload {:option :here})
        (println))
      (println))))


(reset! logging/!printer log-record)
