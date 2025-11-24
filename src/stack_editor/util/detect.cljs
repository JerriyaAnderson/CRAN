
(ns stack-editor.util.detect (:require [clojure.string :as string]))

(defn fuzzy-search [pieces queries]
  (every? (fn [query] (some (fn [piece] (string/includes? piece query)) pieces)) queries))

(defn cirru-vec? [x]
  (if (vector? x) (every? (fn [y] (or (string? y) (cirru-vec? y))) x) false))

(defn strip-atom [token]
  (-> token (string/replace (re-pattern "^@") "") (string/replace (re-pattern "/@") "/")))
