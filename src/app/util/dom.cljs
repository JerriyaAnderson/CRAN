
(ns app.util.dom )

(defn focus-palette! []
  (js/requestAnimationFrame
   (fn []
     (let [target (.querySelector js/document "#command-palette")]
       (if (some? target) (.focus target))))))

(defn focus-rename! [] (-> (.querySelector js/document "#rename-box") (.focus)))
