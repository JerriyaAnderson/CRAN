
(ns app.updater.graph )

(defn load-graph [store op-data]
  (let [root-info (get-in store [:collection :root])] (println root-info))
  store)
