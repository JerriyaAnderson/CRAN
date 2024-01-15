
(ns stack-editor.updater.stack
  (:require [stack-editor.util.analyze :refer [find-path]]))

(defn goto-definition [store op-data op-id]
  (let [writer (:writer store)
        pointer (:pointer writer)
        focus (:focus writer)
        stack (:stack writer)
        pointer (:pointer writer)
        definitions (get-in store [:collection :definitions])
        namespaces (get-in store [:collection :namespaces])
        definition (get stack pointer)]
    (if (= :definitions (:kind writer))
      (let [target (get-in
                     store
                     (concat
                       [:collection :definitions definition]
                       focus))]
        (if (string? target)
          (let [maybe-path (find-path
                             definition
                             namespaces
                             definitions)]
            (if (some? maybe-path)
              (-> store
               (update-in
                 [:writer :stack]
                 (fn [stack] (conj stack maybe-path)))
               (update-in [:writer :pointer] inc))
              (-> store
               (update
                 :notifications
                 (fn [notifications]
                   (into
                     []
                     (cons
                       [op-id (str "\"" definition "\" is not found!")]
                       notifications)))))))
          store))
      store)))

(defn go-back [store op-data] store)
