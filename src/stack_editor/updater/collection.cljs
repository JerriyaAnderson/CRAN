
(ns stack-editor.updater.collection
  (:require [clojure.string :as string]))

(defn set-main [store op-data]
  (let [main-definition op-data]
    (assoc-in store [:collection :main-definition] main-definition)))

(defn remove-this [store op-data op-id]
  (let [writer (:writer store)
        stack (:stack writer)
        pointer (:pointer writer)
        path (get stack pointer)
        path-name (last path)]
    (-> store
     (update
       :collection
       (fn [collection]
         (case
           (first path)
           :definitions
           (update
             collection
             :definitions
             (fn [definitions] (dissoc definitions path-name)))
           :procedures
           (update
             collection
             :procedures
             (fn [procedures]
               (if (contains? (:namespaces collection) path-name)
                 (assoc procedures path-name [])
                 (dissoc procedures path-name))))
           :namespaces
           (-> collection
            (update
              :definitions
              (fn [definitions]
                (->>
                  definitions
                  (filter
                    (fn [entry]
                      (not
                        (string/starts-with?
                          (first entry)
                          (str path-name "/")))))
                  (into {}))))
            (update
              :namespaces
              (fn [namespaces] (dissoc namespaces path-name)))
            (update
              :procudures
              (fn [procedures]
                (println procedures)
                (dissoc procedures path-name))))
           collection)))
     (update-in
       [:writer :stack]
       (fn [stack]
         (cond
           (empty? stack) stack
           (zero? pointer) (subvec stack 1)
           (= (inc pointer) (count stack)) (subvec
                                             stack
                                             0
                                             (dec (count stack)))
           :else (into
                   []
                   (concat
                     (subvec stack 0 pointer)
                     (subvec stack (inc pointer)))))))
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
     (assoc-in (cons :collection (get stack pointer)) tree))))

(defn edit [store op-data]
  (let [path op-data]
    (-> store
     (update
       :writer
       (fn [writer]
         (let [stack (:stack writer) pos (.indexOf stack path)]
           (if (neg? pos)
             (-> writer
              (assoc :focus [])
              (update :pointer (fn [p] (if (empty? stack) p (inc p))))
              (update
                :stack
                (fn [stack]
                  (if (empty? stack)
                    [path]
                    (conj
                      (subvec stack 0 (inc (:pointer writer)))
                      path)))))
             (-> writer (assoc :focus []) (assoc :pointer pos))))))
     (assoc :router {:name :workspace, :data nil}))))

(defn add-definition [store op-data]
  (let [definition-path op-data
        path [:collection :definitions definition-path]
        maybe-definition (get-in store path)
        var-name (last (string/split definition-path "/"))]
    (if (some? maybe-definition)
      store
      (assoc-in store path ["defn" var-name []]))))

(defn edit-ns [store op-data op-id]
  (let [writer (:writer store)
        stack (:stack writer)
        pointer (:pointer writer)
        path (get stack pointer)]
    (if (= (first path) :namespaces)
      (let [ns-name (get-in
                      store
                      (concat [:collection] path (:focus writer)))]
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
                 (fn [stack]
                   (conj
                     (subvec stack 0 (inc pointer))
                     [:namespaces ns-name]))))))
          (update
            store
            :notifications
            (fn [notifications]
              (into
                []
                (cons
                  [op-id (str "\"" ns-name "\" not found")]
                  notifications))))))
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

(defn add-namespace [store op-data]
  (let [namespace' op-data basic-code ["ns" namespace']]
    (-> store
     (assoc-in [:collection :namespaces namespace'] basic-code)
     (assoc-in [:collection :procedures namespace'] []))))

(defn add-procedure [store op-data]
  (let [procedure op-data]
    (assoc-in store [:collection :procedures procedure] [])))
