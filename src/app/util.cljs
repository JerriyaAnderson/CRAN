
(ns app.util
  (:require [app.util.detect :refer [contains-def? =path?]]
            (clojure.set :refer (union))
            (clojure.string :as string)))

(defn collect-defs [node]
  (let [base-result #{(select-keys node [:ns :def])}]
    (if (contains? node :deps)
      (union (apply union (map collect-defs (:deps node))) base-result)
      base-result)))

(defn has-ns? [x] (string/includes? x "/"))

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

(defn helper-notify [op-id data]
  (fn [notifications] (into [] (cons [op-id data] notifications))))

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

(defn now! [] (.now js/performance))

(defn remove-idx [xs idx]
  (let [xs-size (count xs)]
    (cond
      (or (>= idx xs-size) (neg? idx)) xs
      (= xs-size 1) []
      (zero? idx) (subvec xs 1)
      (= idx (dec xs-size)) (subvec xs 0 idx)
      :else (into [] (concat (subvec xs 0 idx) (subvec xs (inc idx)))))))

(defn view-focused [store] (get-in store (make-focus-path store)))
