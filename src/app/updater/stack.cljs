
(ns app.updater.stack
  (:require [clojure.string :as string]
            [app.util.analyze :refer [list-dependent-ns pick-rule parse-ns-deps]]
            [app.util.detect :refer [strip-atom tree-contains? contains-def?]]
            [app.util :refer [remove-idx helper-notify helper-create-def make-path has-ns?]]
            (app.util.stack :refer (push-path push-paths))))

(defn collapse [store op-data op-id]
  (let [cursor op-data]
    (update
     store
     :writer
     (fn [writer]
       (-> writer (assoc :pointer 0) (update :stack (fn [stack] (subvec stack cursor))))))))

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
        {stack :stack, pointer :pointer} writer
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
                                        (cons
                                         {:ns ns-name, :kind :procs, :extra nil}
                                         matched-defs)
                                        matched-defs))))))
                             (filter (fn [xs] (not (empty? xs))))
                             (apply concat))]
          (println "Got new paths:" new-paths)
          (update store :writer (push-paths new-paths)))
      :ns
        (let [ns-list (list-dependent-ns ns-part (:files sepal-ir) pkg)
              new-paths (map (fn [x] [x :ns]) ns-list)]
          (comment println former-stack pointer new-paths)
          (update store :writer (push-paths new-paths)))
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

(defn goto-definition [store op-data op-id]
  (let [forced? op-data
        {pkg :package, files :files} (get-in store [:collection])
        pkg_ (str pkg ".")
        {stack :stack, pointer :pointer} (:writer store)
        code-path (get stack pointer)
        focus (:focus code-path)
        target (strip-atom (get-in store (concat (make-path code-path) focus)))
        ns-deps (parse-ns-deps (get-in files [(:ns code-path) :ns]))
        current-ns-defs (get-in files [(:ns code-path) :defs])
        dep-info (if (has-ns? target)
                   (let [[ns-text def-text] (string/split target "/")
                         maybe-info (get ns-deps ns-text)]
                     (if (and (some? maybe-info) (= :as (:kind maybe-info)))
                       {:ns (:ns maybe-info), :def def-text}
                       nil))
                   (let [maybe-info (get ns-deps target)]
                     (if (and (some? maybe-info) (= :refer (:kind maybe-info)))
                       {:ns (:ns maybe-info), :def target}
                       (if (or (contains? current-ns-defs target) forced?)
                         {:ns (str pkg_ (:ns code-path)), :def target}
                         nil
                         ))))]
    (comment println target dep-info)
    (if (some? dep-info)
      (if (string/starts-with? (:ns dep-info) pkg_)
        (let [existed? (some? (get-in files [(:ns dep-info) :defs (:def dep-info)]))
              shorten-ns (string/replace-first (:ns dep-info) pkg_ "")
              touch-def (fn [store]
                          (println "touching" existed?)
                          (if existed?
                            store
                            (-> store
                                (update-in
                                 [:collection :files]
                                 (helper-create-def
                                  shorten-ns
                                  (:def dep-info)
                                  code-path
                                  (:focus code-path))))))]
          (-> store
              (touch-def)
              (update
               :writer
               (push-path {:kind :defs, :ns shorten-ns, :extra (:def dep-info), :focus [2]}))))
        (-> store
            (update
             :notifications
             (helper-notify op-id (str "External package: " (:ns dep-info))))))
      (-> store (update :notifications (helper-notify op-id (str "Can't find: " target)))))))
