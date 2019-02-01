(ns apptoolkit.http-server.mod
  (:require
   [ring.middleware.defaults :as ring-defaults]
   [ring.middleware.reload :as ring-reload]
   [org.httpkit.server :as http-kit]
   [compojure.core :as compojure]
   [compojure.route :as compojure-route]

   [appkernel.api :as app]))

(tap> ::loading)


(def dev-port 3000)


(defn app-html [app-js-path]
  (str
   "<!DOCTYPE html>
   <html>
   <head>
     <meta charset=\"UTF-8\">
     <meta name=\"viewport\" content=\"minimum-scale=1, initial-scale=1, width=device-width, shrink-to-fit=no\">
     <link rel=\"icon\" href=\"https://clojurescript.org/images/cljs-logo-icon-32.png\">
   </head>
   <body>
     <div id=\"app\">Loading...</div>
     <script src=\"" app-js-path "\" type=\"text/javascript\"></script>
     <script>apptoolkit.browserapp.api.start();</script>
   </body>
   </html>"))

(defn default-routes [app-js-path]
  [(compojure/GET  "/" [] (app-html app-js-path))
   (compojure-route/files "/" {:root "target/public"})
   (compojure-route/not-found "404 - Page not found")])

(defn- app-routes [plain-routes]

  (-> compojure/routes
      (apply plain-routes)

      ;;(ring-reload/wrap-reload) ;; TODO remove for prod

      (ring-defaults/wrap-defaults ring-defaults/site-defaults)))


(defn start!
  [db]
  (let [app-js-path (if (:dev-mode? db) "cljs-out/dev-main.js" "cljs-out/prod-main.js")
        port (get db :http-server/port dev-port)
        routes-from-modules (app/execute-query-sync-and-merge-results db [:http-server/routes])
        plain-routes (into [] routes-from-modules)
        plain-routes (into plain-routes (default-routes app-js-path))]
    (tap> [::start! {:port port}])
    (http-kit/run-server (app-routes plain-routes) {:port port}))
    ;; (app/log :debug ::started {:port port}))
  db)


(app/def-event-handler ::starter
  :event :appkernel/app-started
  :f (fn [db event]
       (start! db)
       db))
