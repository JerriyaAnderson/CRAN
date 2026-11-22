
(ns stack-editor.updater.stack
  (:require [clojure.string :as string]
            [stack-editor.util.analyze
             :refer
             [locate-ns compute-ns list-dependent-ns pick-rule]]
            [stack-editor.util.detect :refer [strip-atom tree-contains? contains-def?]]
            [stack-editor.util
             :refer
             [remove-idx helper-notify helper-create-def helper-put-path make-path]]))

(defn collapse [store op-data op-id]
  (let [cursor op-data]
    (update
     store
     :writer
     (fn [writer]
       (-> writer (assoc :pointer 0) (update :stack (fn [stack] (subvec stack cursor))))))))

(defn goto-definition [store op-data op-id]
  (let [forced? op-data
        writer (:writer store)
        pointer (:pointer writer)
        stack (:stack writer)
        pointer (:pointer writer)
        pkg (get-in store [:collection :package])
        files (get-in store [:collection :files])
        code-path (get stack pointer)
        focus (:focus code-path)
        {current-ns :ns, kind :kind, extra-name :extra} code-path
        drop-pkg (fn [x] (if (string? x) (string/replace x (str pkg ".") "") x))]
    (let [target (get-in store (concat (make-path code-path) focus))]
      (if (string? target)
        (let [stripped-target (strip-atom target)]
          (if forced?
            (if (string/includes? stripped-target "/")
              (let [[ns-part var-part] (string/split stripped-target "/")
                    that-ns (drop-pkg (locate-ns ns-part current-ns files))]
                (if (contains? files that-ns)
                  (if (contains-def? files that-ns var-part)
                    (update store :writer (helper-put-path that-ns var-part [2]))
                    (-> store
                        (update-in
                         [:collection :files]
                         (helper-create-def that-ns var-part code-path focus))
                        (update :writer (helper-put-path that-ns var-part [2]))))
                  (-> store
                      (update
                       :notifications
                       (helper-notify op-id (str "foreign namespace: " that-ns))))))
              (let [ns-part (compute-ns stripped-target current-ns files)
                    that-ns (if (some? ns-part) (drop-pkg ns-part) current-ns)]
                (println "forced piece:" that-ns stripped-target)
                (if (contains? files that-ns)
                  (-> store
                      (update-in
                       [:collection :files]
                       (helper-create-def that-ns stripped-target code-path focus))
                      (update :writer (helper-put-path that-ns stripped-target [2])))
                  (-> store
                      (update
                       :notifications
                       (helper-notify op-id (str "foreign namespace: " that-ns)))))))
            (let [that-ns (drop-pkg (compute-ns stripped-target current-ns files))
                  var-part (last (string/split stripped-target "/"))]
              (println "Search result:" that-ns var-part current-ns)
              (if (contains-def? files that-ns var-part)
                (if (= code-path {:ns that-ns, :kind :defs, :extra var-part})
                  store
                  (update store :writer (helper-put-path that-ns var-part [])))
                (-> store
                    (update
                     :notifications
                     (helper-notify op-id (str "no namespace for: " stripped-target))))))))
        store))))

(defn go-next [store op-data]
  (-> store
      (update
       :writer
       (fn [writer]
         (if (< (:pointer writer) (dec (count (:stack writer))))
           (-> writer (update :pointer inc) (assoc :focus []))
           writer)))))

(defn dependents [store op-data op-id]
  (println "Dependents:" op-data)
  (let [writer (:writer store)
        stack (:stack writer)
        pointer (:pointer writer)
        code-path (get stack pointer)
        {ns-part :ns, kind :kind, extra-name :extra} code-path
        sepal-ir (:collection store)
        former-stack (subvec stack 0 (inc pointer))
        pkg (:package sepal-ir)]
    (case kind
      :defs
        (let [ns-list (list-dependent-ns ns-part (:files sepal-ir) pkg)
              ns-list-more (cons ns-part ns-list)
              new-paths (->> ns-list-more
                             (map
                              (fn [ns-name]
                                (let [file (get-in sepal-ir [:files ns-name])
                                      the-ns-rule (pick-rule (:ns file) ns-part pkg)
                                      method (get the-ns-rule 2)
                                      some-defs (:defs file)]
                                  (comment println "Trying ns:" ns-name method the-ns-rule)
                                  (if (and (= method ":refer")
                                           (let [referred-defs (into
                                                                #{}
                                                                (subvec
                                                                 (get the-ns-rule 3)
                                                                 1))]
                                             (println
                                              "Trying refer:"
                                              referred-defs
                                              extra-name)
                                             (not (contains? referred-defs extra-name))))
                                    (list)
                                    (let [target-name (if (= method ":refer")
                                                        extra-name
                                                        (str (get the-ns-rule 3) extra-name))
                                          matched-defs (->> some-defs
                                                            (filter
                                                             (fn [entry]
                                                               (let [[name-part tree] entry]
                                                                 (comment
                                                                  println
                                                                  "Detecting def:"
                                                                  ns-name
                                                                  name-part)
                                                                 (tree-contains?
                                                                  (subvec tree 2)
                                                                  extra-name))))
                                                            (map
                                                             (fn [entry]
                                                               {:ns ns-name,
                                                                :kind :defs,
                                                                :extra (first entry),
                                                                :focus []})))
                                          proc-matching? (tree-contains?
                                                          (:procs file)
                                                          extra-name)]
                                      (if proc-matching?
                                        (cons [ns-name :procs] matched-defs)
                                        matched-defs))))))
                             (filter (fn [xs] (not (empty? xs))))
                             (apply concat))]
          (println "Got new paths:" new-paths)
          (update
           store
           :writer
           (fn [writer]
             (-> writer
                 (assoc :stack (into [] (concat former-stack new-paths)))
                 (assoc :pointer (if (empty? new-paths) pointer (inc pointer)))))))
      :ns
        (let [ns-list (list-dependent-ns ns-part (:files sepal-ir) (:package sepal-ir))
              new-paths (map (fn [x] [x :ns]) ns-list)]
          (comment println former-stack new-paths pointer)
          (update
           store
           :writer
           (fn [writer]
             (-> writer
                 (assoc :stack (into [] (concat former-stack new-paths)))
                 (assoc :pointer (if (empty? ns-list) pointer (inc pointer)))
                 (assoc :focus [])))))
      store)))

(defn point-to [store op-data op-id]
  (let [pointer op-data] (assoc-in store [:writer :pointer] pointer)))

(defn go-back [store op-data]
  (-> store
      (update
       :writer
       (fn [writer]
         (if (pos? (:pointer writer))
           (-> writer (update :pointer dec) (assoc :focus []))
           writer)))))

(defn shift-one [store op-data op-id]
  (let [pointer op-data]
    (update
     store
     :writer
     (fn [writer]
       (-> writer
           (update :stack (fn [stack] (remove-idx stack pointer)))
           (update
            :pointer
            (fn [pointer]
              (if (= pointer (dec (count (:stack writer)))) (dec pointer) pointer))))))))
