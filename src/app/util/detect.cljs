
(ns app.util.detect (:require [clojure.string :as string]))

(defn cirru-vec? [x]
  (if (vector? x) (every? (fn [y] (or (string? y) (cirru-vec? y))) x) false))

(defn strip-atom [token]
  (-> token
      (string/replace (re-pattern "^@") "")
      (string/replace (re-pattern "\\.$") "")
      (string/replace (re-pattern "/@") "/")))

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

(defn =path? [x y]
  (and (= (:ns x) (:ns y)) (= (:kind x) (:kind y)) (= (:extra x) (:extra y))))
