
(ns stack-editor.updater.collection
  (:require [clojure.string :as string]
            [stack-editor.util :refer [helper-notify helper-put-ns]]))

(defn rename [store op-data op-id]
  (let [[code-path new-form] op-data
        [ns-part kind extra-name] code-path
        pointer (get-in store [:writer :pointer])]
    (case kind
      :ns
        (-> store
            (update-in
             [:collection :files]
             (fn [files] (-> files (dissoc ns-part) (assoc new-form (get files ns-part)))))
            (assoc-in [:writer :stack pointer] [new-form kind]))
      :defs
        (let [[new-ns new-name] (string/split new-form "/"), new-path [new-ns :defs new-name]]
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
                       (assoc-in [new-ns :defs new-name] (get-in files code-path))))))
              (assoc-in [:writer :stack pointer] new-path)))
      (do (println "Cannot rename:" code-path new-form) store))))

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

(defn expand-ns [store op-data op-id]
  (let [writer (:writer store)
        file-path (get (:stack writer) (:pointer writer))
        base-path [:collection :files]
        files (get-in store base-path)
        ns-name (first file-path)
        focus (:focus writer)
        buffer (get-in files (concat file-path focus))]
    (if (and (string? buffer) (string/includes? buffer "/"))
      (let [[ns-x def-x] (string/split buffer "/")
            ns-ast (get-in files [ns-name :ns])
            maybe-rule (first
                        (filter (fn [rule] (= ns-x (get rule 1))) (rest (get ns-ast 2))))]
        (cond
          (nil? maybe-rule)
            (-> store
                (assoc-in (concat base-path file-path focus) def-x)
                (update-in
                 [:collection :files ns-name :ns 2]
                 (fn [require-rules]
                   (conj (into [] require-rules) ["[]" ns-x ":refer" ["[]" def-x]])))
                (update :writer (helper-put-ns ns-name)))
          (= ":refer" (get maybe-rule 2))
            (-> store
                (assoc-in (concat base-path file-path focus) def-x)
                (update-in
                 [:collection :files ns-name :ns 2]
                 (fn [require-rules]
                   (->> require-rules
                        (map
                         (fn [or-rule]
                           (if (= or-rule ":require")
                             ":require"
                             (if (= ns-x (get or-rule 1))
                               (update
                                or-rule
                                3
                                (fn [defs]
                                  (if (contains? (into #{} defs) def-x)
                                    defs
                                    (conj defs def-x))))
                               or-rule)))))))
                (update :writer (helper-put-ns ns-name)))
          (= ":as" (get maybe-rule 2))
            (let [new-ns (last maybe-rule)]
              (-> store (assoc-in (concat base-path file-path focus) (str new-ns "/" def-x))))
          :else (println "Unkown rule:" maybe-rule)))
      (update store :notifications (helper-notify op-id "Not valid ns/def form!")))))

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
    (println "Edit ns:" code-path)
    (if (= (last code-path) :ns)
      (let [guess-ns (get-in store (concat [:collection :files] code-path (:focus writer)))
            ns-name (if (some? guess-ns) (string/replace guess-ns (str pkg ".") "") nil)]
        (if (and (some? ns-name) (some? (get-in store [:collection :files ns-name])))
          (update store :writer (helper-put-ns ns-name))
          (update
           store
           :notifications
           (fn [notifications]
             (into [] (cons [op-id (str "\"" ns-name "\" not found")] notifications))))))
      (let [ns-part (first code-path)] (update store :writer (helper-put-ns ns-part))))))

(defn load-remote [store op-data]
  (let [collection op-data]
    (comment println "loading:" collection)
    (-> store (update :collection (fn [cursor] (merge cursor collection))))))

(defn add-namespace [store op-data]
  (let [namespace' op-data
        basic-code ["ns" (str (get-in store [:collection :package]) "." namespace')]]
    (-> store
        (assoc-in [:collection :files namespace'] {:ns basic-code, :defs {}, :procs []}))))
