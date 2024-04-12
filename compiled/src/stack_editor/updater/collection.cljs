
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
  (let [namespace' op-data]
    (assoc-in store [:collection :namespaces namespace'] [])))

(defn set-main [store op-data]
  (let [main-definition op-data]
    (assoc-in store [:collection :main-definition] main-definition)))

(defn edit-definition [store op-data]
  (let [definition-path op-data]
    (-> store
     (update
       :writer
       (fn [writer]
         (merge
           writer
           {:kind :definitions, :stack [definition-path], :focus []})))
     (assoc-in [:router :name] :workspace))))

(defn edit-procedure [store op-data]
  (let [procedure op-data]
    (-> store
     (update
       :writer
       (fn [writer]
         (merge
           writer
           {:pointer 0, :kind :procedures, :stack [procedure]})))
     (assoc :router {:name :workspace, :data nil}))))

(defn write-code [store op-data]
  (let [tree (:tree op-data)
        focus (:focus op-data)
        writer (:writer store)
        stack (:stack writer)
        kind (:kind writer)
        pointer (:pointer writer)
        clipboard (:clipboard op-data)]
    (-> store
     (assoc-in [:writer :focus] focus)
     (assoc-in [:writer :clipboard] clipboard)
     (assoc-in [:collection kind (get stack pointer)] tree))))

(defn edit-namespace [store op-data]
  (let [namespace' op-data]
    (-> store
     (update
       :writer
       (fn [writer]
         (merge
           writer
           {:pointer 0, :kind :namespaces, :stack [namespace']})))
     (assoc :router {:name :workspace, :data nil}))))

(defn load-remote [store op-data]
  (let [collection op-data]
    (println "loading:" collection)
    (-> store
     (update :collection (fn [cursor] (merge cursor collection)))
     (assoc-in [:router :name] :analyzer))))

(defn remove-this [store op-data op-id]
  (let [writer (:writer store)
        stack (:stack writer)
        pointer (:pointer writer)
        kind (:kind writer)
        path (get stack pointer)]
    (-> store
     (update-in [:collection kind] (fn [dict] (dissoc dict path)))
     (assoc :router {:name :analyzer, :data kind}))))
