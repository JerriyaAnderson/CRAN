
(ns stack-editor.util.analyze
  (:require [clojure.string :as string]))

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
                  matched-rule (first
                                 (filter
                                   (fn 
                                     [rule]
                                     (println
                                       "search rrule:"
                                       rule
                                       short-form
                                       (= ":refer" (get rule 1)))
                                     (and
                                       (= ":refer" (get rule 2))
                                       (some
                                         (fn 
                                           [definition]
                                           (= definition short-form))
                                         (get rule 3))))
                                   rules))]
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
                matched-rule (first
                               (filter
                                 (fn 
                                   [rule]
                                   (println "rule" rule short-form)
                                   (and
                                     (= ":as" (get rule 2))
                                     (= short-form (get rule 3))))
                                 rules))]
            (println "matched-rule" matched-rule)
            (if (some? matched-rule) (get matched-rule 1) nil))))
      nil)))

(defn find-path [piece current-def namespaces definitions]
  (let [current-ns (first (string/split current-def "/"))
        this-namespace (get namespaces current-ns)]
    (if (string/includes? piece "/")
      (let [that-ns (first (string/split piece "/"))
            that-var (last (string/split piece "/"))
            maybe-namespace (locate-ns that-ns current-ns namespaces)]
        (if (some? maybe-namespace)
          (let [maybe-that-def (str maybe-namespace "/" that-var)]
            (if (contains? definitions maybe-that-def)
              {:ok true, :data maybe-that-def}
              {:ok false,
               :data "variable in that namespace not existed"}))
          {:ok false, :data "namespace not drafted yet"}))
      (let [maybe-this-def (str current-ns "/" piece)]
        (if (contains? definitions maybe-this-def)
          {:ok true, :data maybe-this-def}
          (let [maybe-that-ns (locate-ns-by-var
                                piece
                                current-ns
                                namespaces)]
            (if (some? maybe-that-ns)
              (if (contains? namespaces maybe-that-ns)
                {:ok true, :data (str maybe-that-ns "/" piece)}
                {:ok false, :data "probably foreign namespace"})
              {:ok false,
               :data "can find a namespace from :refer"})))))))
