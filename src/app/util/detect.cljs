
(ns app.util.detect (:require [clojure.string :as string]))

(defn fuzzy-search [pieces queries]
  (every?
   (fn [query] (some (fn [piece] (string/includes? (str piece) query)) pieces))
   queries))

(defn contains-def? [files ns-part name-part]
  (println "Contains def:" ns-part name-part)
  (if (contains? files ns-part)
    (let [dict (get-in files [ns-part :defs])] (contains? dict name-part))
    false))

(defn tree-contains? [tree x]
  (if (string? tree)
    (= tree x)
    (if (empty? tree)
      false
      (let [cursor (first tree), at-head? (tree-contains? cursor x)]
        (if at-head? true (recur (rest tree) x))))))

(defn cirru-vec? [x]
  (if (vector? x) (every? (fn [y] (or (string? y) (cirru-vec? y))) x) false))

(defn def-order [x y]
  (cond
    (and (:circular? x) (not (:circular? y))) -1
    (and (:circular? y) (not (:circular? x))) 1
    (and (:external? x) (not (:external? y))) 1
    (and (:external? y) (not (:external? x))) -1
    :else (compare (str (:ns x) "/" (:def x)) (str (:ns y) "/" (:def y)))))

(defn use-vector? [xs] (= "[]" (first xs)))

(defn =def? [x y] (and (= (:ns x) (:ns y)) (= (:def x) (:def y))))

(defn strip-atom [token]
  (-> token
      (string/replace (re-pattern "^@") "")
      (string/replace (re-pattern "\\.$") "")
      (string/replace (re-pattern "/@") "/")))

(defn find-by [f xs0]
  (loop [i 0, xs xs0]
    (if (empty? xs) -1 (let [x (first xs)] (if (f x) i (recur (inc i) (rest xs)))))))

(defn =path? [x y]
  (and (= (:ns x) (:ns y)) (= (:kind x) (:kind y)) (= (:extra x) (:extra y))))
