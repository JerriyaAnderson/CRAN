
(ns app.updater.modal )

(defn mould [store op-data op-id]
  (let [modal op-data] (update store :modal-stack (fn [stack] (conj stack modal)))))

(defn recycle [store op-data op-id]
  (update store :modal-stack (fn [stack] (into [] (butlast stack)))))
