(ns flexlist-client.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [re-frisk.core :refer [enable-re-frisk!]]
              [flexlist-client.events]
              [flexlist-client.subs]
              [flexlist-client.routes :as routes]
              [flexlist-client.views :as views]
              [flexlist-client.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (enable-re-frisk!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  ;;(routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
