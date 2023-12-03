(ns clojure-mutest.html-formatter
  (:require [hiccup.core :as hiccup]
            [clojure.string :as str]))

;; output
;; {:mutants [{:killed false, :filename "./resources/testp/src/testp/core.clj", :line 3, :column 26, :before "not", :after "boolean"}]}

(defn create-html [output]
  (let [mutants (:mutants output)
        total-tests (count mutants)
        total-killed (count (filter :killed mutants))
        total-survived (- total-tests total-killed)
        sorted-mutants (sort-by :killed mutants)]
    (hiccup/html
     [:html
      [:head
       [:title "Mutest HTML Report"]
       [:link {:rel "stylesheet" :type "text/css" :href "https://fonts.googleapis.com/css2?family=Roboto+Mono:wght@400&family=Roboto:wght@400&display=swap"}]
       [:style (str "body {font-family: 'Roboto', sans-serif; margin: 0; padding: 0;}"
                    "h1 {font-family: 'Roboto', sans-serif; font-weight: 400; text-align: center; padding: 20px; margin: 0;}"
                    "table {border-collapse: collapse; margin: auto;}"
                    "th, td {border: 1px solid #ddd; padding: 12px; text-align: left; font-size: 14px;}"
                    ".monospace {font-family: 'Roboto Mono', monospace; white-space: nowrap;}"
                    ".center {text-align: center;}"
                    ".killed {background-color: #dff0d8;}"
                    ".failed {background-color: #f2dede;}"
                    ".success {color: green;}"
                    "#hideKilledLabel {margin-left: auto; margin-right: auto; display: block; text-align: center;}"
                    "#hideKilled {padding: 5px;}")]]
      [:body
       [:h1 {:class (if (= total-survived 0) "success" "incomplete")} "Mutant Test Report"]
       [:label {:for "hideKilled" :id "hideKilledLabel"}
        [:input {:type "checkbox" :id "hideKilled" :checked false}]
        "Hide Killed Tests"]
       [:table
        [:thead
         [:tr
          [:th "File"]
          [:th "Line"]
          [:th "Column"]
          [:th "Killed"]
          [:th "Before"]
          [:th "After"]]]
        [:tbody
         (for [mutant sorted-mutants]
           [:tr {:class (if (:killed mutant) "killed" "failed")}
            [:td (:filename mutant)]
            [:td (:line mutant)]
            [:td (:column mutant)]
            [:td (if (:killed mutant) "Yes" "No")]
            [:td (:before mutant)]
            [:td (:after mutant)]])]
        [:tr
         [:td {:colspan 5} "Total Tests"]
         [:td {:class "monospace"} total-tests]]
        [:tr
         [:td {:colspan 5} "Total Killed"]
         [:td {:class "monospace"} total-killed]]
        [:tr
         [:td {:colspan 5} "Total Survived"]
         [:td {:class "monospace"} total-survived]]
        [:script
         "document.addEventListener('DOMContentLoaded', function() {",
         "  var hideKilledCheckbox = document.getElementById('hideKilled');",
         "  hideKilledCheckbox.addEventListener('change', function() {",
         "    var hideKilled = this.checked;",
         "    var rows = document.querySelectorAll('.killed');",
         "    rows.forEach(function(row) {",
         "      row.style.display = hideKilled ? 'none' : '';",
         "    });",
         "  });",
         "});"]]]])))