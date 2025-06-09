
(ns stack-editor.util.querystring (:require [clojure.string :as string]))

(defn parse-query [search]
  (if (= search "")
    {}
    (let [content (subs search 1)
          pairs (map (fn [piece] (string/split piece "=")) (string/split content "&"))]
      (into {} pairs))))
