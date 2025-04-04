
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
                  matched-rule (->>
                                 rules
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
                matched-rule (->>
                               rules
                               (filter
                                 (fn 
                                   [rule]
                                   (println "rule" rule short-form)
                                   (and
                                     (= ":as" (get rule 2))
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

(defn respond [status data] {:ok status, :data data})

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
              (respond true maybe-that-def)
              (respond
                false
                "variable in that namespace not existed")))
          (respond false "namespace not drafted yet")))
      (let [maybe-this-def (str current-ns "/" piece)]
        (if (contains? definitions maybe-this-def)
          (respond true maybe-this-def)
          (let [maybe-that-ns (locate-ns-by-var
                                piece
                                current-ns
                                namespaces)]
            (if (some? maybe-that-ns)
              (if (contains? namespaces maybe-that-ns)
                (let [that-def (str maybe-that-ns "/" piece)]
                  (if (contains? definitions that-def)
                    (respond true that-def)
                    (respond false "undefined def")))
                (respond false "probably foreign namespace"))
              (respond false "can find a namespace from :refer"))))))))
