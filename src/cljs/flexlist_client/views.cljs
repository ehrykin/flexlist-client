(ns flexlist-client.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [flexlist-client.views.login-or-register-view :as login-or-register-view]
              [flexlist-client.views.login-view :as login-view]
              [flexlist-client.views.register-view :as register-view]
              [flexlist-client.views.after-register-view :as after-register-view]
              [flexlist-client.views.create-list-manually-or-by-csv-view :as create-list-manually-or-by-csv-view]
              [flexlist-client.views.create-list-structure-view :as create-list-structure-view]
              [flexlist-client.views.about-view :as about-view]))


;; main

(defn- panels [panel-name]
  (case panel-name
    :login-or-register-panel                [login-or-register-view/login-or-register-panel]
    :login-panel                            [login-view/login-panel]
    :register-panel                         [register-view/register-panel]
    :after-register-panel                   [after-register-view/after-register-panel]
    :create-list-manually-or-by-csv-panel   [create-list-manually-or-by-csv-view/create-list-manually-or-by-csv-panel]
    :create-list-structure-panel            [create-list-structure-view/create-list-structure-panel]
    :about-panel                            [about-view/about-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [re-com/v-box
       :height "100%"
       :width "100%"
       :align :stretch
       :children [[panels @active-panel]]])))
