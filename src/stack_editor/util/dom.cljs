
(ns stack-editor.util.dom)

(defn focus-palette! []
  (js/requestAnimationFrame
    (fn []
      (let [target (.querySelector js/document "#command-palette")]
        (.focus target)))))
