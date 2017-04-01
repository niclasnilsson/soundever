(ns soundever.app
  (:require [reagent.core :as r :refer [atom]]))

(defn log [message]
  (.log js/console (pr-str message)))

(defonce data
  (r/atom
    {:volume 0
     ; :volume-stream (range 0 100 5)}))
     :volume-stream [0 10 20 30 40 nil nil 70 80 90]}))

(defn set-volume [player-id volume]
 (let [player (.querySelector js/document player-id)]
   (aset player "volume" (/ volume 100))))

(defn consume-value [coll-name]
  (let [coll (coll-name @data)
        value (first coll)]
    (swap! data assoc coll-name (rest coll))
    value))

(def volume-updater (js/setInterval
                     (fn []
                       (let [value (consume-value :volume-stream)]
                         (if value
                           (do
                             (set-volume "#player1" value)))))
                     1000))

(defn slider [param min max value]
  [:input {:type "range" :min min :max max :defaultValue value
           :style {:width "100%"}
           :on-change
             (fn [e]
               ; TODO: Är det här rätt tänkt?
               (swap! data assoc param (int (.-target.value e)))
               (set-volume "#player1" (int (.-target.value e))))}])

(defn audio-player []
  [:audio {:id :player1 :autoPlay true :controls true}
    [:source {:src "https://arthead.io/sounds/CafeJazz.mp3"}]])

(defn app-view []
  (let [volume (:volume @data)]
    [:div
     [slider :volume 0 100 20]
     [:h3 (str "Volume is " volume)]
     [audio-player]]))

(defn init []
  (r/render-component [app-view]
                      (.getElementById js/document "container")))
