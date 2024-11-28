
(ns stack-editor.updater.notification)

(defn remove-since [store op-data]
  (let [pos op-data]
    (-> store
     (update
       :notifications
       (fn [notifications] (take pos notifications))))))

(defn remove-one [store op-data]
  (let [notification-id op-data]
    (-> store
     (update
       :notifications
       (fn [notifications]
         (filterv
           (fn [notification]
             (not= notification-id (first notification)))
           notifications))))))

(defn add-one [store op-data op-id]
  (let [notification op-data]
    (-> store
     (update
       :notifications
       (fn [notifications]
         (into
           []
           (cons [op-id notification] (take 6 notifications))))))))
