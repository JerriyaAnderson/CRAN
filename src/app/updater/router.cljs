
(ns app.updater.router
  (:require [app.util.stack :refer [get-path]] [clojure.string :as string]))

(defn toggle-palette [store op-data op-id] (update-in store [:router :show-palette?] not))

(defn route [store op-data] (let [router op-data] (assoc store :router router)))

(defn open-file-tree [store op-data op-id]
  (let [code-path (get-path store)]
    (-> store
        (assoc-in [:router :name] :file-tree)
        (assoc-in [:graph :ns-path] (vec (string/split (:ns code-path) "."))))))
