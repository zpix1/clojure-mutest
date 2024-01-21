(ns clojure-mutest.html-formatter
  (:require [hiccup.core :as hiccup]
            [clojure.string :as str]))

(defn create-html [mutest-results]
  (let [mutants (:mutants mutest-results)
        total-tests (count mutants)
        total-killed (count (filter :killed mutants))
        total-survived (- total-tests total-killed)
        sorted-mutants (sort-by :killed mutants)]
    (hiccup/html
     [:html
      [:head
       [:title "Clojure Mutest Report"]
       [:link {:rel "stylesheet" :type "text/css" :href "https://fonts.googleapis.com/css2?family=Roboto+Mono:wght@400&family=Roboto:wght@400&display=swap"}]
       [:style (str "body {font-family: 'Roboto', sans-serif; margin: 0; padding: 0;}"
                    "h1 {font-family: 'Roboto', sans-serif; font-weight: 400; text-align: center; padding: 20px; margin: 0;}"
                    "table {border-collapse: collapse; margin: auto;}"
                    "th, td {border: 1px solid #ddd; padding: 12px; text-align: left; font-size: 14px;}"
                    ".monospace {font-family: 'Roboto Mono', monospace; white-space: pre;}"
                    ".center {text-align: center;}"
                    ".killed {background-color: #dff0d8;}"
                    ".skipped {background-color: #f0e168;}"
                    ".failed {background-color: #f2dede;}"
                    ".success {color: green;}"
                    "#hideKilledLabel {padding: 5px; margin-left: auto; margin-right: auto; display: block; text-align: center;}"
                    "#hideKilled {padding: 5px;}"
                    ".copy-btn {cursor: pointer; padding: 5px; background-color: #007bff; color: #fff; border: none; border-radius: 5px; margin-left: 5px;}")]]
      [:body
       [:h1 {:class (if (= total-survived 0) "success" "incomplete")} (str "Clojure Mutest Report " (format "%.3f%%" (/ total-killed total-tests 0.01)))]
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
          [:th "Skipped"]
          [:th "Before"]
          [:th "After"]
          [:th "Hash"]
          [:th "Copy"]
          [:th "Diff"]]]
        [:tbody
         (for [mutant sorted-mutants]
           [:tr {:class (if (:killed mutant) "killed"
                            (if (:skipped mutant) "skipped" "failed"))}
            [:td (:filename mutant)]
            [:td (:line mutant)]
            [:td (:column mutant)]
            [:td (if (:killed mutant) "Yes" "No")]
            [:td (if (:skipped mutant) "Yes" "No")]
            [:td {:class "monospace"} (:before mutant)]
            [:td {:class "monospace"} (:after mutant)]
            [:td {:class "monospace"
                  :title (:hash mutant)}
             (subs (:hash mutant) 0 7)]
            [:td
             [:button.copy-btn
              {:onclick (str "copyToClipboard('" {:hash (:hash mutant)} "')")}
              "Copy"]]
            [:td {:class "monospace"} (or (:git-diff-path mutant) "")]])
         [:tr
          [:td {:colspan 9} "Total Tests"]
          [:td {:class "monospace"} total-tests]]
         [:tr
          [:td {:colspan 9} "Total Killed"]
          [:td {:class "monospace"} total-killed]]
         [:tr
          [:td {:colspan 9} "Total Survived"]
          [:td {:class "monospace"} total-survived]]
         [:script
          "document.addEventListener('DOMContentLoaded', function() {",
          "  var hideKilledCheckbox = document.getElementById('hideKilled');",
          "  hideKilledCheckbox.addEventListener('change', function() {",
          "    var hideKilled = this.checked;",
          "    var rows = document.querySelectorAll('.killed, .skipped');",
          "    rows.forEach(function(row) {",
          "      row.style.display = hideKilled ? 'none' : '';",
          "    });",
          "  });",
          "});",
          "  function copyToClipboard(text) {",
          "    var input = document.createElement('textarea');",
          "    input.value = text;",
          "    document.body.appendChild(input);",
          "    input.select();",
          "    document.execCommand('copy');",
          "    document.body.removeChild(input);",
          "  }"]]]]])))