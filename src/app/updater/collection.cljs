
(ns app.updater.collection
  (:require [clojure.string :as string]
            [app.util
             :refer
             [helper-notify helper-put-ns make-path view-focused make-focus-path]]
            [app.util.detect :refer [=path?]]))

(defn rename [store op-data op-id]
  (let [[code-path new-form] op-data
        {ns-part :ns, kind :kind, extra-name :extra, focus :focus} code-path
        pointer (get-in store [:writer :pointer])]
    (case kind
      :ns
        (-> store
            (update-in
             [:collection :files]
             (fn [files] (-> files (dissoc ns-part) (assoc new-form (get files ns-part)))))
            (assoc-in
             [:writer :stack pointer]
             {:ns new-form, :kind :ns, :extra nil, :focus focus}))
      :defs
        (let [[new-ns new-name] (string/split new-form "/")]
          (-> store
              (update-in
               [:collection :files]
               (fn [files]
                 (if (= new-ns ns-part)
                   (update-in
                    files
                    [ns-part :defs]
                    (fn [dict]
                      (-> dict (dissoc extra-name) (assoc new-name (get dict extra-name)))))
                   (-> files
                       (update-in [ns-part :defs] (fn [dict] (dissoc dict extra-name)))
                       (assoc-in
                        [new-ns :defs new-name]
                        (get-in
                         files
                         (if (= :defs kind) [ns-part :defs extra-name] [ns-part kind])))))))
              (assoc-in
               [:writer :stack pointer]
               {:ns new-ns, :kind :defs, :extra new-name, :focus focus})))
      (do (println "Cannot rename:" code-path new-form) store))))

(defn remove-this [store op-data op-id]
  (let [writer (:writer store)
        stack (:stack writer)
        pointer (:pointer writer)
        {ns-part :ns, kind :kind, extra-name :extra} (get stack pointer)]
    (-> store
        (update-in
         [:collection :files]
         (fn [files]
           (case kind
             :defs (update-in files [ns-part :defs] (fn [defs] (dissoc defs extra-name)))
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
        clipboard (:clipboard op-data)
        path-info (get stack pointer)]
    (-> store
        (assoc-in [:writer :stack pointer :focus] focus)
        (assoc-in [:writer :clipboard] clipboard)
        (assoc-in (make-path path-info) tree))))

(defn hydrate [store op-data op-id]
  (let [writer (:writer store)
        collection (:collection store)
        code-path (get (:stack writer) (:pointer writer))]
    (println code-path)
    (assoc-in store (concat (make-path code-path) (:focus code-path)) op-data)))

(defn edit [store op-data]
  (let [path op-data]
    (-> store
        (update
         :writer
         (fn [writer]
           (let [stack (:stack writer)
                 pos (loop [xs stack, x 0]
                       (if (empty? xs)
                         -1
                         (do
                          (println path (first xs))
                          (if (=path? path (first xs)) x (recur (rest xs) (inc x))))))]
             (if (neg? pos)
               (-> writer
                   (assoc :focus [2])
                   (update :pointer (fn [p] (if (empty? stack) p (inc p))))
                   (update
                    :stack
                    (fn [stack]
                      (if (empty? stack)
                        [path]
                        (conj (subvec stack 0 (inc (:pointer writer))) path)))))
               (-> writer (assoc :pointer pos))))))
        (assoc :router {:name :workspace, :data nil}))))

(defn add-definition [store op-data]
  (let [[that-ns that-name] op-data
        path [:collection :files that-ns :defs that-name]
        maybe-definition (get-in store path)]
    (if (some? maybe-definition) store (assoc-in store path ["defn" that-name []]))))

(defn edit-ns [store op-data op-id]
  (let [writer (:writer store)
        stack (:stack writer)
        pointer (:pointer writer)
        code-path (get stack pointer)
        pkg (get-in store [:collection :package])]
    (comment println "Edit ns:" code-path)
    (if (= (:kind code-path) :ns)
      (let [guess-ns (view-focused store)
            ns-name (if (some? guess-ns) (string/replace guess-ns (str pkg ".") "") nil)]
        (if (and (some? ns-name) (some? (get-in store [:collection :files ns-name])))
          (update store :writer (helper-put-ns ns-name))
          (update
           store
           :notifications
           (fn [notifications]
             (into [] (cons [op-id (str "\"" ns-name "\" not found")] notifications))))))
      (update store :writer (helper-put-ns (:ns code-path))))))

(defn load-remote [store op-data]
  (let [collection op-data]
    (comment println "loading:" collection)
    (-> store (update :collection (fn [cursor] (merge cursor collection))))))

(defn add-namespace [store op-data]
  (let [namespace' op-data
        basic-code ["ns" (str (get-in store [:collection :package]) "." namespace')]]
    (-> store
        (assoc-in [:collection :files namespace'] {:ns basic-code, :defs {}, :procs []}))))
