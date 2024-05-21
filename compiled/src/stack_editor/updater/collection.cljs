
(ns stack-editor.updater.collection
  (:require [clojure.string :as string]))

(defn add-definition [store op-data]
  (let [definition-path op-data
        path [:collection :definitions definition-path]
        maybe-definition (get-in store path)
        var-name (last (string/split definition-path "/"))]
    (if (some? maybe-definition)
      store
      (assoc-in store path ["defn" var-name []]))))

(defn add-procedure [store op-data]
  (let [procedure op-data]
    (assoc-in store [:collection :procedures procedure] [])))

(defn add-namespace [store op-data]
  (let [namespace' op-data basic-code ["ns" namespace']]
    (assoc-in store [:collection :namespaces namespace'] basic-code)))

(defn set-main [store op-data]
  (let [main-definition op-data]
    (assoc-in store [:collection :main-definition] main-definition)))

(defn write-code [store op-data]
  (let [tree (:tree op-data)
        focus (:focus op-data)
        writer (:writer store)
        stack (:stack writer)
        pointer (:pointer writer)
        clipboard (:clipboard op-data)]
    (-> store
     (assoc-in [:writer :focus] focus)
     (assoc-in [:writer :clipboard] clipboard)
     (assoc-in (cons :collection (get stack pointer)) tree))))

(defn edit [store op-data]
  (let [path op-data]
    (-> store
     (update
       :writer
       (fn [writer]
         (-> writer
          (assoc :focus [])
          (update :pointer (fn [p] (if (zero? p) p (inc p))))
          (update
            :stack
            (fn [stack]
              (conj (subvec stack 0 (inc (:pointer writer))) path))))))
     (assoc :router {:name :workspace, :data nil}))))

(defn edit-ns [store op-data op-id]
  (let [writer (:writer store)
        stack (:stack writer)
        pointer (:pointer writer)
        path (get stack pointer)]
    (if (= (first path) :namespaces)
      store
      (let [ns-part (first (string/split (last path) "/"))]
        (update
          store
          :writer
          (fn [writer]
            (-> writer
             (update :pointer inc)
             (update
               :stack
               (fn [stack]
                 (conj
                   (subvec stack 0 (inc pointer))
                   [:namespaces ns-part]))))))))))

(defn load-remote [store op-data]
  (let [collection op-data]
    (comment println "loading:" collection)
    (-> store
     (update :collection (fn [cursor] (merge cursor collection)))
     (assoc-in [:router :name] :analyzer))))

(defn remove-this [store op-data op-id]
  (let [writer (:writer store)
        stack (:stack writer)
        pointer (:pointer writer)
        path (get stack pointer)]
    (-> store
     (update-in
       [:collection (first path)]
       (fn [dict] (dissoc dict (last path))))
     (assoc :router {:name :analyzer, :data (first path)}))))
