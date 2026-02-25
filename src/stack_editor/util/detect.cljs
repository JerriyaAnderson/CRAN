
(ns stack-editor.util.detect (:require [clojure.string :as string]))

(defn cirru-vec? [x]
  (if (vector? x) (every? (fn [y] (or (string? y) (cirru-vec? y))) x) false))

(defn strip-atom [token]
  (-> token (string/replace (re-pattern "^@") "") (string/replace (re-pattern "/@") "/")))

(defn fuzzy-search [pieces queries]
  (every?
   (fn [query] (some (fn [piece] (string/includes? (str piece) query)) pieces))
   queries))
