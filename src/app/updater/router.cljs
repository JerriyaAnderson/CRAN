
(ns app.updater.router )

(defn toggle-palette [store op-data op-id] (update-in store [:router :show-palette?] not))

(defn route [store op-data] (let [router op-data] (assoc store :router router)))
