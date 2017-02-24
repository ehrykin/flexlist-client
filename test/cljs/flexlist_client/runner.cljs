(ns flexlist-client.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [flexlist-client.core-test]))

(doo-tests 'flexlist-client.core-test)
