
(ns app.util.analyze
  (:require [clojure.string :as string] [app.util.detect :refer [contains-def?]]))

(defn pick-rule [ns-block ns-name pkg]
  (let [full-ns (str pkg "." ns-name), rules (subvec (get ns-block 2) 1)]
    (loop [left-rules rules]
      (if (empty? left-rules)
        nil
        (let [cursor (first left-rules)]
          (comment println "Picking" cursor full-ns)
          (if (= full-ns (get cursor 1)) cursor (recur (rest left-rules))))))))

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

(defn compute-ns [piece current-ns files]
  (println "compute-ns" piece current-ns)
  (if (string/includes? piece "/")
    (let [[that-ns that-value] (string/split piece "/")] (locate-ns that-ns current-ns files))
    (if (contains-def? files current-ns piece)
      current-ns
      (locate-ns-by-var piece current-ns files))))

(defn parse-rule [dict rule]
  (let [clean-rule (if (= "[]" (first rule)) (subvec rule 1) rule)
        ns-text (first clean-rule)
        binding-rule (subvec clean-rule 1)]
    (loop [left-binding binding-rule, result dict]
      (comment println "doing loop:" left-binding result)
      (if (< (count left-binding) 2)
        result
        (let [kind (first left-binding), data (get left-binding 1)]
          (recur
           (subvec left-binding 2)
           (cond
             (= ":as" kind) (assoc result data {:kind :as, :ns ns-text, :text data})
             (= ":refer" kind)
               (->> data
                    (filter (fn [x] (not= x "[]")))
                    (map (fn [x] [x {:kind :refer, :ns ns-text, :text x}]))
                    (into {})
                    (merge result))
             :else result)))))))

(defn pick-dep [token]
  (cond
    (string/blank? token) nil
    (string/starts-with? token ":") nil
    (string/starts-with? token ".") nil
    (string/starts-with? token "|") nil
    (string/starts-with? token "#") nil
    (string/starts-with? token "[") nil
    (string/starts-with? token "'") nil
    (string/starts-with? token "{") nil
    (string/starts-with? token "%") nil
    (= token "--") nil
    (string/includes? token "/")
      (let [[ns-piece def-piece] (string/split token "/")]
        {:kind :ns, :data ns-piece, :extra def-piece})
    (string/includes? token ".")
      (let [[def-piece prop-piece] (string/split token ".")] {:kind :def, :data def-piece})
    (string/starts-with? token "@") {:kind :def, :data (subs token 1)}
    :else {:kind :def, :data token}))

(defn parse-ns-deps [expression]
  (let [branches (->> (subvec expression 2) (filter (fn [expr] (= ":require" (first expr)))))]
    (if (empty? branches) {} (doall (reduce parse-rule {} (rest (first branches)))))))

(defn pick-def-deps [expression internal-ns file pkg]
  (let [external? (fn [ns-text] (not (string/starts-with? ns-text (str pkg "."))))
        ns-deps (parse-ns-deps (:ns file))]
    (->> (subvec expression 2)
         (flatten)
         (map pick-dep)
         (filter some?)
         (map
          (fn [info]
            (case (:kind info)
              :def
                (let [def-text (:data info), defs (:defs file)]
                  (cond
                    (contains? ns-deps def-text)
                      (let [using-mapping (get ns-deps def-text)]
                        (if (= :refer (:kind using-mapping))
                          (let [ns-text (:ns using-mapping)]
                            {:ns ns-text, :def def-text, :external? (external? ns-text)})
                          nil))
                    (contains? defs def-text)
                      {:ns (str pkg "." internal-ns), :def def-text, :external? false}
                    :else nil))
              :ns
                (let [{ns-text :data, def-text :extra} info]
                  (if (contains? ns-deps ns-text)
                    (let [using-mapping (get ns-deps ns-text)]
                      (if (= :as (:kind using-mapping))
                        (let [ns-text (:ns using-mapping)]
                          {:ns ns-text, :def def-text, :external? (external? ns-text)})
                        nil))
                    nil))
              nil)))
         (filter some?)
         (into #{}))))

(defn expand-deps-tree [internal-ns def-text files pkg parents]
  (let [this-file (get files internal-ns)
        def-expr (get-in this-file [:defs def-text])
        def-deps (pick-def-deps def-expr internal-ns this-file pkg)
        stamp {:ns internal-ns, :def def-text}
        base-dep {:ns internal-ns, :def def-text, :external? false}]
    (if (contains? parents stamp)
      base-dep
      (assoc
       base-dep
       :deps
       (->> def-deps
            (map
             (fn [dep-info]
               (if (:external? dep-info)
                 dep-info
                 (let [child-internal-ns (string/replace-first
                                          (:ns dep-info)
                                          (str pkg ".")
                                          "")
                       child-def (:def dep-info)
                       next-parents (conj parents stamp)]
                   (expand-deps-tree child-internal-ns child-def files pkg next-parents)))))
            (into #{}))))))

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
