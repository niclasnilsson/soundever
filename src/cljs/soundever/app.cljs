(ns soundever.app
  (:require [reagent.core :as r :refer [atom]]))

(defn log [message]
  (.log js/console (pr-str message)))

(def config
  {
    :traffic-load
      { :type :volume
        :player-id "player1"
        :sample-rate 1000
        :min 0
        :max 200
        :sound-source "https://arthead.io/sounds/CafeJazz.mp3"}
    :number-of-users
      { :type :volume
        :player-id "player2"
        :sample-rate 5000
        :min 0
        :max 1400
        :sound-source "https://arthead.io/sounds/crow.mp3"}})

(defonce data
  (r/atom
    {
      :traffic-load-stream    [0 10 20 30 40 50 60 70 80 90]
      :number-of-users-stream [0 100 120 100 120 300 600 700]}))

(defn set-volume [player-id percent]
  (let [player (.querySelector js/document (str "#" player-id))]
    (aset player "volume" percent)))

(defn pop-value [coll-name]
  (let [coll (coll-name @data)
        value (first coll)]
    (swap! data assoc coll-name (rest coll))
    value))

(defn percent [min max value]
  (Math/min (/ value (- max min)) 1.0))

(defn update-volume [stream-id player-id min max]
  (fn [value]
    (let [value (pop-value stream-id)]
      (if value
        (set-volume player-id (percent min max value))))))

(defn create-timer [f interval]
  (js/setInterval f interval))

(defn kw [s]
  (keyword (subs s 1)))

(defn setup-stream [name config]
  (let [{:keys [player-id min max sample-rate]} config
        stream-id (kw (str name "-stream"))
        update-fn (update-volume stream-id player-id min max)]
    (create-timer update-fn sample-rate)))

(defn setup-system [config]
  (doseq [[name stream-config] config]
    (setup-stream name stream-config)))

(defn audio-player [player-id src]
  [:audio {:id player-id :autoPlay true :controls true :loop true}
    [:source {:src src}]])

(defn player [player-id source]
  [:div {:key player-id}
   (audio-player player-id source)])

(defn players [config]
  (map
    #(player (:player-id %1) (:sound-source %1))
    (vals config)))

(defn app-view []
  (let [volume (:volume @data)]
    [:div
     [:h3 (str "Volume is " volume)]
     (map
       #(player (str "player" %) "https://arthead.io/sounds/CafeJazz.mp3")
       (range 1 3))]))

(defn init []
  (r/render-component [app-view]
                      (.getElementById js/document "container")))
  ;(set-volume "player1" 0)
  ;(set-volume "player2" 0))

(setup-system config)

; (defn slider [param min max value]
;   [:input {:type "range" :min min :max max :defaultValue value
;            :style {:width "100%"}
;            :on-change
;              (fn [e]
;                ; TODO: Är det här rätt tänkt?
;                (swap! data assoc param (int (.-target.value e)))
;                (set-volume "#player1" (int (.-target.value e))))}])
