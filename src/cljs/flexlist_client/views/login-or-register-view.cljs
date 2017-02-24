(ns flexlist-client.views.login-or-register-view
    (:require [re-com.core   :as re-com]
              [flexlist-client.views.login-view :as login-view]
              [flexlist-client.views.register-view :as register-view]))

(defn login-or-register-panel []
    [re-com/v-box
     ;;:size "auto"
     :align-self :center
     ;;:margin      "5%"
     :width  "50%"
     :children [
                 [re-com/title
                  :label "Fill out the fields below to register"
                  :level :level2
                  ]
                 [re-com/title
                  :label "or use the login form if you allready have an account."
                  :level :level2
                  ]
                 [re-com/h-box
                  ;;:width "80%"
                  :children [
                   [login-view/login-panel]
                   [register-view/register-panel]
                  ]
                 ]
              ]
     ]
  )
