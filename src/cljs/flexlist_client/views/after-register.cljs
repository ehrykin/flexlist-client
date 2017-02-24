(ns flexlist-client.views.after-register-view
    (:require [re-com.core   :as re-com]
              [flexlist-client.views.login-view :as login-view]))

(defn after-register-panel []
    [re-com/v-box
     :size "auto"
     :align-self :center
     :margin      "5%"
     :children [
                 [re-com/title
                   :label "You was successfulle signed. Please login."
                   :level :level1]

                 [login-view/login-panel]

                ]
     ]
  )
