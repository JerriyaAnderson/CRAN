
(ns app.util.analyze
  (:require [clojure.string :as string] [app.util.detect :refer [contains-def? use-vector?]]))

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
    (string/starts-with? token "\\") nil
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

(defn extract-deps [expression internal-ns file pkg]
  (let [external? (fn [ns-text] (not (string/starts-with? ns-text (str pkg "."))))
        ns-deps (parse-ns-deps (:ns file))]
    (->> expression
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
        stamp {:ns internal-ns, :def def-text}
        base-dep {:ns internal-ns, :def def-text, :external? false, :circular? false}]
    (if (nil? def-expr)
      (assoc base-dep :external? true)
      (if (contains? parents stamp)
        (assoc base-dep :circular? true)
        (assoc
         base-dep
         :deps
         (let [def-deps (if (some? def-expr)
                          (extract-deps (subvec def-expr 2) internal-ns this-file pkg)
                          nil)]
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
                (into #{}))))))))

(defn list-dependent-ns [ns-name files pkg]
  (let [full-ns (str pkg "." ns-name)
        pick-ns (fn [xs] (if (use-vector? xs) (get xs 1) (first xs)))]
    (->> files
         (filter
          (fn [entry]
            (let [[ns-part file] entry
                  ns-expr (:ns file)
                  ns-rules (->> (subvec ns-expr 2)
                                (map rest)
                                (apply concat)
                                (map pick-ns)
                                (into #{}))]
              (comment println "Search:" ns-name ns-rules)
              (contains? ns-rules full-ns))))
         (map first)
         (into #{}))))
