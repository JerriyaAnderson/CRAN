
(ns stack-editor.util
  (:require [stack-editor.util.detect :refer [contains-def? =path?]]
            (clojure.string :as string)))

(defn remove-idx [xs idx]
  (let [xs-size (count xs)]
    (cond
      (or (>= idx xs-size) (neg? idx)) xs
      (= xs-size 1) []
      (zero? idx) (subvec xs 1)
      (= idx (dec xs-size)) (subvec xs 0 idx)
      :else (into [] (concat (subvec xs 0 idx) (subvec xs (inc idx)))))))

(defn make-path [info]
  (let [kind (:kind info)]
    (if (= kind :defs)
      [:collection :files (:ns info) :defs (:extra info)]
      [:collection :files (:ns info) kind])))

(defn make-focus-path [store]
  (let [writer (:writer store)
        pointer (:pointer writer)
        stack (:stack writer)
        code-path (get stack pointer)]
    (concat (make-path code-path) (:focus code-path))))

(defn helper-put-ns [ns-name]
  (fn [writer]
    (-> writer
        (update :pointer inc)
        (update
         :stack
         (fn [stack]
           (conj
            (subvec stack 0 (inc (:pointer writer)))
            {:ns ns-name, :kind :ns, :extra nil, :focus []}))))))

(defn helper-put-path [ns-part name-part focus]
  (fn [writer]
    (-> writer
        (update
         :stack
         (fn [stack]
           (let [next-pointer (inc (:pointer writer))
                 code-path {:ns ns-part, :kind :defs, :extra name-part, :focus focus}]
             (if (< (dec (count stack)) next-pointer)
               (conj stack code-path)
               (if (=path? code-path (get stack next-pointer))
                 stack
                 (conj (into [] (subvec stack 0 next-pointer)) code-path))))))
        (update :pointer inc))))

(defn drop-pkg [x pkg] (if (string? x) (string/replace x (str pkg ".") "") x))

(defn helper-notify [op-id data]
  (fn [notifications] (into [] (cons [op-id data] notifications))))

(defn view-focused [store] (get-in store (make-focus-path store)))

(defn make-short-path [info]
  (let [kind (:kind info)]
    (if (= kind :defs) [(:ns info) :defs (:extra info)] [(:ns info) kind])))

(defn helper-create-def [ns-part name-part code-path focus]
  (fn [files]
    (if (contains-def? files ns-part name-part)
      files
      (assoc-in
       files
       [ns-part :defs name-part]
       (let [as-fn? (and (not (empty? focus)) (zero? (last focus)))]
         (if as-fn?
           (let [expression (get-in
                             files
                             (concat (make-short-path code-path) (butlast focus)))]
             (if (> (count expression) 1)
               ["defn" name-part (subvec expression 1)]
               ["defn" name-part []]))
           ["def" name-part []]))))))

(defn now! [] (.now js/performance))
