
(ns stack-editor.util )

(defn remove-idx [xs idx]
  (let [xs-size (count xs)]
    (cond
      (or (>= idx xs-size) (neg? idx)) xs
      (= xs-size 1) []
      (zero? idx) (subvec xs 1)
      (= idx (dec xs-size)) (subvec xs 0 idx)
      :else (into [] (concat (subvec xs 0 idx) (subvec xs (inc idx)))))))

(defn now! [] (.now js/performance))
