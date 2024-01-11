
(ns stack-editor.updater.notification)

(defn add-one [store op-data]
  (let [notification op-data]
    (-> store
     (update
       :notifications
       (fn [notifications]
         (into [] (cons notification notifications)))))))

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
