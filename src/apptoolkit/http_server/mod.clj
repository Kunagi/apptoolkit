(ns apptoolkit.http-server.mod
  (:require
   [ring.middleware.defaults :as ring-defaults]
   [ring.middleware.params :as ring-params]
   [ring.middleware.reload :as ring-reload]
   [ring.middleware.oauth2 :as ring-oauth]
   [org.httpkit.server :as http-kit]
   [compojure.core :as compojure]
   [compojure.route :as compojure-route]

   [appkernel.api :as app]
   [appkernel.paths :as paths]

   [apptoolkit.http-server.oauth :as oauth]))

(tap> ::loading)


(def dev-port 3000)

(def preloader-css
  ".preloader {margin: 100px auto 0; width: 66px; height: 12px;}
div.preloader div {color: #000; margin: 5px 0; text-transform: uppercase; font-family: 'Arial', sans-serif; font-size: 9px; letter-spacing: 2px;}
.preloader .line {width: 1px; height: 12px; background: #000; margin: 0 1px; display: inline-block; animation: opacity-1 1000ms infinite ease-in-out;}
.preloader .line-1 { animation-delay: 800ms; }
.preloader .line-2 { animation-delay: 600ms; }
.preloader .line-3 { animation-delay: 400ms; }
.preloader .line-4 { animation-delay: 200ms; }
.preloader .line-6 { animation-delay: 200ms; }
.preloader .line-7 { animation-delay: 400ms; }
.preloader .line-8 { animation-delay: 600ms; }
@keyframes opacity-1 {0% {opacity: 1;} 50% {opacity: 0;} 100% {opacity: 1;}}
@keyframes opacity-2 {0% {opacity: 1; height: 15px;} 50% {opacity: 0; height: 12px;} 100% {opacity: 1; height: 15px;}}")

(def preloader-html
  "<div class='preloader'> <div>Loading</div> <span class='line line-1'></span> <span class='line line-2'></span> <span class='line line-3'></span> <span class='line line-4'></span> <span class='line line-5'></span> <span class='line line-6'></span> <span class='line line-7'></span> <span class='line line-8'></span> </div>")

(defn app-html [page-config]
  (fn [request]
    (let [browserapp-config {:auth/user-id (-> request :session :auth/user-id)
                             :app/name (:app-name page-config)}]
      (str
       "<!DOCTYPE html>
       <html>
       <head>
         <meta charset=\"UTF-8\">
         <meta name=\"viewport\" content=\"minimum-scale=1, initial-scale=1, width=device-width, shrink-to-fit=no\">
         <link rel=\"icon\" href=\"https://clojurescript.org/images/cljs-logo-icon-32.png\">"
       "<style>"
       preloader-css
       "</style>"
       " </head>
       <body>
         <div id=\"app\">"
       preloader-html
       "</div>
         <script src=\"" (:app-js-path page-config) nil "\"\"></script>
         <script>" (-> page-config :app-name (.replace "-" "_")) ".main.start('" (pr-str browserapp-config) "');</script>
       </body>
       </html>"))))

(defn default-routes [page-config]
  [(compojure/GET  "/"               [] {:status 301 :headers {"Location" "/ui/"}})
   (compojure/GET  "/ui"             [] {:status 301 :headers {"Location" "/ui/"}})
   (compojure/GET  "/ui/**"          [] (app-html page-config))
   (compojure/GET "/ping"            [] (fn [request] (str request)))
   (compojure/GET "/oauth/completed" [] oauth/handle-oauth-completed)
   (compojure-route/files "/"        {:root "target/public"}) ;; TODO remove in prod
   (compojure-route/resources "/"    {:root "public"})
   (compojure-route/not-found        "404 - Page not found")])

(defn- app-routes [plain-routes oauth2-config]

  (-> compojure/routes
      (apply plain-routes)

      (ring-oauth/wrap-oauth2 oauth2-config)

      (ring-params/wrap-params)

      (ring-defaults/wrap-defaults
       (-> ring-defaults/site-defaults
           (assoc-in [:session :cookie-attrs :same-site] :lax)))))


(defn start!
  [db]
  (let [page-config {:app-js-path (if (:dev-mode? db) "/cljs-out/dev-main.js" "/cljs-out/prod-main.js")
                     :app-name (:app/name db)}
        port (get db :http-server/port dev-port)
        oauth2-config (oauth/create-ring-oauth2-config db)
        routes-from-modules (app/q db [:http-server/routes])
        plain-routes (into [] routes-from-modules)
        plain-routes (into plain-routes (default-routes page-config))]
    (tap> [::start! {:port port}])
    (http-kit/run-server (app-routes plain-routes oauth2-config) {:port port}))
    ;; (app/log :debug ::started {:port port}))
  db)


(app/def-event-handler ::starter
  :event :app/started
  :f (fn [db event]
       (start! db)
       db))
