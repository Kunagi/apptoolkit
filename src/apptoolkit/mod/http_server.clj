(ns apptoolkit.mod.http-server
  (:require
   [ring.middleware.defaults :as ring-defaults]
   [ring.middleware.reload :as ring-reload]
   [org.httpkit.server :as http-kit]
   [compojure.core :as compojure]
   [compojure.route :as compojure-route]

   [appkernel.api :as appkernel]))


(def port 3000)

(def home
  "<!DOCTYPE html>
  <html>
  <head>
    <meta charset=\"UTF-8\">
    <meta name=\"viewport\" content=\"minimum-scale=1, initial-scale=1, width=device-width, shrink-to-fit=no\">
    <link rel=\"icon\" href=\"https://clojurescript.org/images/cljs-logo-icon-32.png\">
  </head>
  <body>
    <div id=\"app\">Loading...</div>
    <script src=\"/cljs-out/dev-main.js\" type=\"text/javascript\"></script>
    <script>apptoolkit.browserapp.api.start();</script>
  </body>
  </html>")

(def default-routes
  [(compojure/GET  "/" [] home)
   (compojure-route/files "/" {:root "target/public"})
   (compojure-route/not-found "404 - Page not found")])

(defn- app-routes [plain-routes]

  (-> compojure/routes
      (apply plain-routes)

      ;;(ring-reload/wrap-reload) ;; TODO remove for prod

      (ring-defaults/wrap-defaults ring-defaults/site-defaults)))


(defn start!
  [db]
  (let [;; routes-from-modules [] (app/extensions-get-concatinated db :http-server/routes :routes)
        routes-from-modules (appkernel/execute-query-sync-and-merge-results db [:http-server/routes])
        plain-routes (into [] routes-from-modules)
        plain-routes (into plain-routes default-routes)]
    (http-kit/run-server (app-routes plain-routes) {:port port}))
    ;; (app/log :debug ::started {:port port}))
  db)


;; (app/def-module :http-server

;;   :on-start #(start! %))
