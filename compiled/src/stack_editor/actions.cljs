
(ns stack-editor.actions
  (:require [cljs.reader :refer [read-string]]
            [ajax.core :refer [GET POST json-request-format]]))

(defn load-collection! [dispatch!]
  (GET
    "http://localhost:7010"
    {:error-handler (fn [error] (println error)),
     :handler
     (fn [response]
       (println response)
       (dispatch! :collection/load (read-string response)))}))

(defn submit-collection! [collection dispatch!]
  (POST
    "http://localhost:7010"
    {:format (json-request-format),
     :error-handler (fn [error] (println error)),
     :body (pr-str collection),
     :handler (fn [response] (println response))}))
