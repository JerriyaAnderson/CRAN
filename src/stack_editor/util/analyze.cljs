
(ns stack-editor.util.analyze (:require [clojure.string :as string]))

(defn locate-ns-by-var [short-form this-namespace namespaces]
  (println "searching" short-form this-namespace namespaces)
  (if (nil? this-namespace)
    nil
    (if (contains? namespaces this-namespace)
      (let [namespace-data (get namespaces this-namespace)]
        (println "namespace-data" namespace-data)
        (if (and (nil? namespace-data) (<= (count namespace-data) 2))
          nil
          (if (>= (count namespace-data) 3)
            (let [required (get namespace-data 2)
                  rules (subvec required 1)
                  matched-rule (->> rules
                                    (filter
                                     (fn [rule]
                                       (println
                                        "search rrule:"
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

(defn locate-ns [short-form this-namespace namespaces]
  (if (nil? this-namespace)
    nil
    (if (contains? namespaces this-namespace)
      (let [namespace-data (get namespaces this-namespace)]
        (if (or (nil? namespace-data) (<= (count namespace-data) 2))
          nil
          (let [required (get namespace-data 2)
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

(defn compute-ns [piece current-def namespaces definitions]
  (let [current-ns (first (string/split current-def "/"))
        this-namespace (get namespaces current-ns)]
    (if (string/includes? piece "/")
      (let [[that-ns that-value] (string/split piece "/")]
        (locate-ns that-ns current-ns namespaces))
      (let [maybe-this-def (str current-ns "/" piece)]
        (if (contains? definitions maybe-this-def)
          current-ns
          (locate-ns-by-var piece current-ns namespaces))))))
