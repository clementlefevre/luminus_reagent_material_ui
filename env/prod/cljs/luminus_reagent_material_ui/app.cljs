(ns luminus-reagent-material-ui.app
  (:require [luminus-reagent-material-ui.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
