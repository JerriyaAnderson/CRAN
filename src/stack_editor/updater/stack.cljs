
(ns stack-editor.updater.stack
  (:require [clojure.string :as string]
            [stack-editor.util.analyze :refer [find-path]]
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

(defn helper-create-def [path focus current-def target]
  (fn [definitions]
    (assoc
      definitions
      path
      (if (and (not (empty? focus)) (zero? (last focus)))
        (let [expression (-> definitions
                          (get current-def)
                          (get-in (butlast focus)))]
          (if (> (count expression) 1)
            ["defn" (strip-atom target) (subvec expression 1)]
            ["defn" (strip-atom target) []]))
        ["defn" (strip-atom target) []]))))

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
        (let [maybe-path (find-path
                           (strip-atom target)
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
                store
                (let [ns-part (first (string/split current-def "/"))
                      path (str ns-part "/" (strip-atom target))]
                  (-> store
                   (update-in
                     [:collection :definitions]
                     (helper-create-def path focus current-def target))
                   (update :writer (helper-put-path path)))))
              (-> store
               (update
                 :notifications
                 (fn [notifications]
                   (into
                     []
                     (cons
                       [op-id (:data maybe-path)]
                       notifications))))))))
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
