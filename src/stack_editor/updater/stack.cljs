
(ns stack-editor.updater.stack
  (:require [clojure.string :as string]
            [stack-editor.util.analyze :refer [find-path locate-ns]]
            [stack-editor.util.detect :refer [strip-atom]]))

(defn collapse [store op-data op-id]
  (let [cursor op-data]
    (update
      store
      :writer
      (fn [writer]
        (-> writer
         (assoc :pointer 0)
         (update :stack (fn [stack] (subvec stack cursor))))))))

(defn helper-put-path [path]
  (fn [writer]
    (-> writer
     (update
       :stack
       (fn [stack]
         (let [next-pointer (inc (:pointer writer))]
           (if (< (dec (count stack)) next-pointer)
             (conj stack [:definitions path])
             (if (= path (last (get stack next-pointer)))
               stack
               (conj
                 (into [] (subvec stack 0 next-pointer))
                 [:definitions path]))))))
     (update :pointer inc)
     (assoc :focus []))))

(defn helper-notify [op-id data]
  (fn [notifications] (into [] (cons [op-id data] notifications))))

(defn helper-create-def [ns-part focus current-def var-part]
  (fn [definitions]
    (assoc
      definitions
      (str ns-part "/" var-part)
      (if (and (not (empty? focus)) (zero? (last focus)))
        (let [expression (-> definitions
                          (get current-def)
                          (get-in (butlast focus)))]
          (if (> (count expression) 1)
            ["defn" var-part (subvec expression 1)]
            ["defn" var-part []]))
        ["defn" var-part []]))))

(defn goto-definition [store op-data op-id]
  (let [forced? op-data
        writer (:writer store)
        pointer (:pointer writer)
        focus (:focus writer)
        stack (:stack writer)
        pointer (:pointer writer)
        definitions (get-in store [:collection :definitions])
        namespaces (get-in store [:collection :namespaces])
        current-def (last (get stack pointer))]
    (println "writer" writer)
    (let [target (get-in
                   store
                   (concat [:collection] (get stack pointer) focus))]
      (if (string? target)
        (let [stripped-target (strip-atom target)
              maybe-path (find-path
                           stripped-target
                           current-def
                           namespaces
                           definitions)]
          (println "maybe-path" maybe-path)
          (if (:ok maybe-path)
            (let [path (:data maybe-path)]
              (if (= path (last (get stack pointer)))
                store
                (update store :writer (helper-put-path path))))
            (if forced?
              (if (string/includes? target "/")
                (let [[ns-part var-part] (string/split target "/")
                      current-ns (first (string/split current-def "/"))
                      that-ns (locate-ns
                                ns-part
                                current-ns
                                namespaces)]
                  (if (contains? namespaces that-ns)
                    (let [new-path (str that-ns "/" var-part)]
                      (if (contains? definitions new-path)
                        (update
                          store
                          :writer
                          (helper-put-path new-path))
                        (-> store
                         (update-in
                           [:collection :definitions]
                           (helper-create-def
                             that-ns
                             focus
                             current-def
                             var-part))
                         (update :writer (helper-put-path new-path)))))
                    (-> store
                     (update
                       :notifications
                       (helper-notify
                         op-id
                         (str "foreign namespace: " that-ns))))))
                (let [ns-part (first (string/split current-def "/"))
                      new-path (str ns-part "/" stripped-target)]
                  (-> store
                   (update-in
                     [:collection :definitions]
                     (helper-create-def
                       ns-part
                       focus
                       current-def
                       stripped-target))
                   (update :writer (helper-put-path new-path)))))
              (-> store
               (update
                 :notifications
                 (helper-notify op-id (:data maybe-path)))))))
        store))))

(defn go-next [store op-data]
  (-> store
   (update
     :writer
     (fn [writer]
       (if (< (:pointer writer) (dec (count (:stack writer))))
         (-> writer (update :pointer inc) (assoc :focus []))
         writer)))))

(defn point-to [store op-data op-id]
  (let [pointer op-data] (assoc-in store [:writer :pointer] pointer)))

(defn go-back [store op-data]
  (-> store
   (update
     :writer
     (fn [writer]
       (if (pos? (:pointer writer))
         (-> writer (update :pointer dec) (assoc :focus []))
         writer)))))
