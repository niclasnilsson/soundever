(ns soundever.app
  (:require [reagent.core :as r :refer [atom]]))

(defn log [message]
  (.log js/console (pr-str message)))

(defn set-volume [player-id volume]
 (let [player (.querySelector js/document player-id)]
   (aset player "volume" (/ volume 100))))

(defonce data
  (r/atom
    {:volume 0}))

(defn slider [param min max value]
  [:input {:type "range" :min min :max max :defaultValue value
           :style {:width "100%"}
           :on-change
             (fn [e]
               ; TODO: Är det här rätt tänkt?
               (swap! data assoc param (int (.-target.value e)))
               (set-volume "#player1" (int (.-target.value e))))}])

(defn audio-player []
  [:audio {:id :player1 :autoPlay false :controls true}
    [:source {:src "https://arthead.io/sounds/crow.mp3"}]])

(defn app-view []
  (let [volume (:volume @data)]
    [:div
     [slider :volume 0 100 20]
     [:h3 (str "Volume is " volume)]
     [audio-player]]))

(defn init []
  (r/render-component [app-view]
                      (.getElementById js/document "container")))
