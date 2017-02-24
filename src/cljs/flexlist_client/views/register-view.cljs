(ns flexlist-client.views.register-view
    (:require [re-frame.core :as re-frame]
              [re-com.core   :as re-com]
              [reagent.core  :as reagent]
              [ajax.core :as ajax]
              [day8.re-frame.http-fx]))

;;-----------------handlers---------------------

(re-frame/reg-event-fx
 :register
 (fn [db [_ info]]
   (let [username  (:username  info)
         password  (:password  info)
         password2 (:password2 info)
         email     (:email     info)]
    {:db (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show
     :http-xhrio {:method          :post
                  :uri             "/register-user"
                  :params          {:username username
                                    :password password
                                    :password2  password2
                                    :email    email}
                  :timeout         5000
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:after-registration-success info]
                  :on-failure      [:after-registration-failure info]}}
   )
  )
)

(re-frame/reg-event-db
 :after-registration-success
 (fn [db [_ info result]]
   (:db (assoc-in db [:db :active-panel] :after-register-panel))
  )
)

(re-frame/reg-event-db ;; TODO: replace with failure handler
 :after-registration-failure
  (fn [db [_ info result]]
   (:db (assoc-in db [:db :active-panel] :after-register-panel))
  )
)

;;------------------views--------------------

(defn register-panel []
  (let [username (reagent/atom "")
        password (reagent/atom "")
        password2 (reagent/atom "")
        email (reagent/atom "")]
    [re-com/v-box
     :size "auto"
     :align-self :center
     :margin      "5%"
     :children [
                 [re-com/title
                   :label "Register"
                   :level :level1]

                 [re-com/title
                   :label "Username:"
                   :level :level3]
                [re-com/input-text
                 :model        username
                 :on-change   #(reset! username %)]

                 [re-com/title
                   :label "Password:"
                   :level :level3]
                [re-com/input-text
                 :model        password
                 :on-change   #(reset! password %)]

                 [re-com/title
                   :label "Confirm Password:"
                   :level :level3]
                [re-com/input-text
                 :model        password2
                 :on-change   #(reset! password2 %)]

                 [re-com/title
                   :label "E-mail address:"
                   :level :level3]
                [re-com/input-text
                 :model        email
                 :on-change   #(reset! email %)]

                 [re-com/gap :size "10px"]

                [re-com/button
                 :label    "Register"
                 :on-click  #(re-frame/dispatch [:register {:username  @username
                                                            :password  @password
                                                            :password2 @password2
                                                            :email     @email}])]

                 ]
     ])
  )
