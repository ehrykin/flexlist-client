(ns flexlist-client.db
  (:require [re-frame-datatable.core :as dt]))

(def default-db
    {:userid           nil
     :lists            {
                         1     { :label  "Test list 1"
                                 :colls   [{::dt/column-key [:index]       ::dt/column-label "Index"}
                                          {::dt/column-key [:city-name]    ::dt/column-label "City"}
                                          {::dt/column-key [:population]   ::dt/column-label "Population"}]
                                 :data    [{:index  1   :city-name   "Moscow"   :population 10000000}
                                          {:index  2   :city-name   "London"   :population 4000000}
                                          {:index  2   :city-name   "Paris"    :population 5000000}]
                                }
                        }
     :active-list-id   1
     :active-panel     :login-or-register-panel ;; :login-or-register-panel ;; :create-list-structure-panel ;; :create-list-panel
     :show-twirly      false
     :show-add-grid-data-dialog? false
     :show-add-grid-field-dialog? false
    }
)

