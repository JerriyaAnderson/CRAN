
(ns app.util.stack (:require [app.util.detect :refer [=path?]]))

(defn path-index-of
  ([xs y] (path-index-of xs y 0))
  ([xs y idx]
   (if (empty? xs) -1 (if (=path? y (first xs)) idx (recur (rest xs) y (inc idx))))))

(defn push-paths [new-paths]
  (fn [writer]
    (if (empty? new-paths)
      writer
      (let [stack (:stack writer), pointer (:pointer writer)]
        (if (and (= 1 (count new-paths)) (>= (path-index-of stack (first new-paths)) 0))
          (do
           (println "hit" (path-index-of stack (first new-paths)))
           (assoc writer :pointer (path-index-of stack (first new-paths))))
          (cond
            (empty? stack)
              {:stack (into [] new-paths), :pointer 0, :focus (:focus (first new-paths))}
            (= (inc pointer) (count stack))
              (-> writer
                  (assoc :stack (into [] (concat stack new-paths)))
                  (update :pointer inc)
                  (assoc :focus (:focus (first new-paths))))
            :else
              (-> writer
                  (assoc
                   :stack
                   (into
                    []
                    (concat
                     (subvec stack 0 (inc pointer))
                     new-paths
                     (subvec stack (inc pointer)))))
                  (update :pointer inc)
                  (assoc :focus (:focus (first new-paths))))))))))

(defn push-path [x] (push-paths [x]))

(defn get-path [store]
  (let [writer (:writer store), {stack :stack, pointer :pointer} writer] (get stack pointer)))
