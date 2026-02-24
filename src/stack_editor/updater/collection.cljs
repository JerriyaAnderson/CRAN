
(ns stack-editor.updater.collection (:require [clojure.string :as string]))

(defn rename [store op-data op-id]
  (let [[kind old-name new-name] op-data
        pointer (get-in store [:writer :pointer])
        swap-name (fn [dict]
                    (-> dict (dissoc old-name) (assoc new-name (get dict old-name))))]
    (if (= kind :namespaces)
      (-> store
          (update-in [:collection kind] swap-name)
          (update-in [:procedures kind] swap-name)
          (update-in
           [:collection :definitions]
           (fn [dict]
             (->> dict
                  (map
                   (fn [pair]
                     (let [[path value] pair]
                       [(string/replace-first path (str old-name "/") (str new-name "/"))
                        value])))
                  (into {}))))
          (assoc-in [:writer :stack pointer] [kind new-name]))
      (-> store
          (update-in [:collection kind] swap-name)
          (assoc-in [:writer :stack pointer] [kind new-name])))))

(defn set-main [store op-data]
  (let [main-definition op-data]
    (assoc-in store [:collection :main-definition] main-definition)))

(defn remove-this [store op-data op-id]
  (let [writer (:writer store)
        stack (:stack writer)
        pointer (:pointer writer)
        path (get stack pointer)
        [ns-part kind extra-name] path]
    (-> store
        (update-in
         [:collection :files]
         (fn [files]
           (case kind
             :defs
               (update-in
                files
                [ns-part :defs extra-name]
                (fn [defs] (dissoc defs extra-name)))
             :procs (assoc-in files [ns-part :procs] [])
             :ns (dissoc files ns-part)
             files)))
        (update-in
         [:writer :stack]
         (fn [stack]
           (cond
             (empty? stack) stack
             (zero? pointer) (subvec stack 1)
             (= (inc pointer) (count stack)) (subvec stack 0 (dec (count stack)))
             :else (into [] (concat (subvec stack 0 pointer) (subvec stack (inc pointer)))))))
        (update-in
         [:writer :pointer]
         (fn [pointer] (if (pos? pointer) (dec pointer) pointer))))))

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
        (assoc-in (cons :collection (cons :files (get stack pointer))) tree))))

(defn hydrate [store op-data op-id]
  (let [writer (:writer store)
        collection (:collection store)
        path (concat [:collection] (get (:stack writer) (:pointer writer)) (:focus writer))]
    (println path)
    (assoc-in store path op-data)))

(defn edit [store op-data]
  (let [path op-data]
    (-> store
        (update
         :writer
         (fn [writer]
           (let [stack (:stack writer), pos (.indexOf stack path)]
             (if (neg? pos)
               (-> writer
                   (assoc :focus [])
                   (update :pointer (fn [p] (if (empty? stack) p (inc p))))
                   (update
                    :stack
                    (fn [stack]
                      (if (empty? stack)
                        [path]
                        (conj (subvec stack 0 (inc (:pointer writer))) path)))))
               (-> writer (assoc :focus []) (assoc :pointer pos))))))
        (assoc :router {:name :workspace, :data nil}))))

(defn add-definition [store op-data]
  (let [definition-path op-data
        path [:collection :definitions definition-path]
        maybe-definition (get-in store path)
        var-name (last (string/split definition-path "/"))]
    (if (some? maybe-definition) store (assoc-in store path ["defn" var-name []]))))

(defn edit-ns [store op-data op-id]
  (let [writer (:writer store)
        stack (:stack writer)
        pointer (:pointer writer)
        path (get stack pointer)]
    (if (= (first path) :namespaces)
      (let [ns-name (get-in store (concat [:collection] path (:focus writer)))]
        (if (some? (get-in store [:collection :namespaces ns-name]))
          (update
           store
           :writer
           (fn [writer]
             (-> writer
                 (update :pointer inc)
                 (assoc :focus [])
                 (update
                  :stack
                  (fn [stack] (conj (subvec stack 0 (inc pointer)) [:namespaces ns-name]))))))
          (update
           store
           :notifications
           (fn [notifications]
             (into [] (cons [op-id (str "\"" ns-name "\" not found")] notifications))))))
      (let [ns-part (first (string/split (last path) "/"))]
        (update
         store
         :writer
         (fn [writer]
           (-> writer
               (update :pointer inc)
               (assoc :focus [])
               (update
                :stack
                (fn [stack] (conj (subvec stack 0 (inc pointer)) [:namespaces ns-part]))))))))))

(defn load-remote [store op-data]
  (let [collection op-data]
    (comment println "loading:" collection)
    (-> store (update :collection (fn [cursor] (merge cursor collection))))))

(defn add-namespace [store op-data]
  (let [namespace' op-data
        basic-code ["ns" (str (get-in store [:collection :package]) "." namespace')]]
    (-> store
        (assoc-in [:collection :files namespace'] {:ns basic-code, :defs {}, :procs []}))))

(defn add-procedure [store op-data]
  (let [procedure op-data] (assoc-in store [:collection :procedures procedure] [])))
