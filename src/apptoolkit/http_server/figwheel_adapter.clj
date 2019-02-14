(ns apptoolkit.http-server.figwheel-adapter
  (:require
   [appkernel.load-this-namespace-to-activate-dev-mode]
   [appkernel.api :as app]

   [apptoolkit.http-server.mod :as http-server]))

(tap> ::loading)

(defn ring-handler-for-figwheel [request]
  (let [href (str "http://localhost:" http-server/dev-port)]
    (if (and (= :get (:request-method request))
             (= "/"  (:uri request)))
      (do
        (app/start! {})
        {:status 302
         :headers {"Location" href}})
      {:status 404
       :headers {"Content-Type" "text/plain"}
       :body (str "404 - Page not found\n"
                  "\n"
                  "This is the ring handler for figwheel.\n"
                  "Goto " href)})))
