
(ns app.util.analyze
  (:require [clojure.string :as string] [app.util.detect :refer [contains-def?]]))

(defn locate-ns [short-form current-ns files]
  (if (nil? current-ns)
    nil
    (if (contains? files current-ns)
      (let [ns-data (get-in files [current-ns :ns])]
        (if (or (nil? ns-data) (<= (count ns-data) 2))
          nil
          (let [required (get ns-data 2)
                rules (subvec required 1)
                matched-rule (->> rules
                                  (filter
                                   (fn [rule]
                                     (println "rule" rule short-form)
                                     (and (= ":as" (get rule 2))
                                          (= short-form (get rule 3)))))
                                  (first))]
            (println "matched-rule" matched-rule)
            (if (some? matched-rule) (get matched-rule 1) nil))))
      nil)))

(defn locate-ns-by-var [short-form current-ns files]
  (println "ns by var:" short-form current-ns)
  (if (nil? current-ns)
    nil
    (if (contains? files current-ns)
      (let [ns-data (get-in files [current-ns :ns])]
        (println "ns-data:" ns-data)
        (if (and (nil? ns-data) (<= (count ns-data) 2))
          nil
          (if (>= (count ns-data) 3)
            (let [required (get ns-data 2)
                  rules (subvec required 1)
                  matched-rule (->> rules
                                    (filter
                                     (fn [rule]
                                       (println
                                        "Search rule:"
                                        rule
                                        short-form
                                        (= ":refer" (get rule 1)))
                                       (and (= ":refer" (get rule 2))
                                            (some
                                             (fn [definition] (= definition short-form))
                                             (get rule 3)))))
                                    (first))]
              (println "rules" matched-rule)
              (if (some? matched-rule) (get matched-rule 1) nil))
            nil)))
      nil)))

(defn compute-ns [piece current-ns files]
  (println "compute-ns" piece current-ns)
  (if (string/includes? piece "/")
    (let [[that-ns that-value] (string/split piece "/")] (locate-ns that-ns current-ns files))
    (if (contains-def? files current-ns piece)
      current-ns
      (locate-ns-by-var piece current-ns files))))

(defn list-dependent-ns [ns-name files pkg]
  (let [full-ns (str pkg "." ns-name)]
    (->> files
         (filter
          (fn [entry]
            (let [[ns-part file] entry
                  ns-rules (->> (get-in file [:ns 2])
                                rest
                                (map (fn [xs] (get xs 1)))
                                (into #{}))]
              (comment println "Search:" ns-name ns-rules)
              (contains? ns-rules full-ns))))
         (map first))))

(defn pick-rule [ns-block ns-name pkg]
  (let [full-ns (str pkg "." ns-name), rules (subvec (get ns-block 2) 1)]
    (loop [left-rules rules]
      (if (empty? left-rules)
        nil
        (let [cursor (first left-rules)]
          (comment println "Picking" cursor full-ns)
          (if (= full-ns (get cursor 1)) cursor (recur (rest left-rules))))))))
