(ns sablono.util-test
  (:require [sablono.core :refer-macros [with-base-url]]
            [sablono.util :as u]
            #?(:clj [clojure.test :refer :all])
            #?(:cljs [cljs.test :refer-macros [are is testing]])
            #?(:cljs [devcards.core :refer-macros [deftest]]))
  #?(:cljs (:import goog.Uri)))

(deftest test-camel-case-keys
  (are [attrs expected]
      (= expected (u/camel-case-keys attrs))
    {:id "x"}
    {:id "x"}
    {:class "x"}
    {:class "x"}
    {:http-equiv "Expires"}
    {:httpEquiv "Expires"}
    {:style {:z-index 1000}}
    {:style {:zIndex 1000}}
    {:on-click '(fn [e] (let [m {:a-b "c"}]))}
    {:onClick '(fn [e] (let [m {:a-b "c"}]))}))

(deftest test-html-to-dom-attrs
  (are [attrs expected]
      (= expected (u/html-to-dom-attrs attrs))
    {:id "x"}
    {:id "x"}
    {:class "x"}
    {:className "x"}
    {:http-equiv "Expires"}
    {:httpEquiv "Expires"}
    {:style {:z-index 1000}}
    {:style {:zIndex 1000}}
    {:on-click '(fn [e] (let [m {:a-b "c"}]))}
    {:onClick '(fn [e] (let [m {:a-b "c"}]))}))

(deftest test-element?
  (is (u/element? [:div]))
  (is (not (u/element? nil)))
  (is (not (u/element? [])))
  (is (not (u/element? 1)))
  (is (not (u/element? "x"))))

#?(:cljs
   (deftest test-as-str
     (are [args expected]
         (= expected (apply u/as-str args))
       ["foo"] "foo"
       [:foo] "foo"
       [100] "100"
       ["a" :b 3] "ab3"
       [(Uri. "/foo")] "/foo"
       [(Uri. "localhost:3000/foo")] "localhost:3000/foo")))

(deftest test-camel-case
  (are [attr expected]
      (= expected (u/camel-case attr))
    nil nil
    "" ""
    :data :data
    :data-toggle :data-toggle
    :http-equiv :httpEquiv
    :aria-checked :aria-checked))

#?(:cljs
   (deftest test-to-uri
     (testing "with no base URL"
       (are [obj expected]
           (= expected (u/to-str (u/to-uri obj)))
         "foo" "foo"
         "/foo/bar" "/foo/bar"
         "/foo#bar" "/foo#bar"))
     (testing "with base URL"
       (with-base-url "/foo"
         (are [obj expected]
             (= expected (u/to-str (u/to-uri obj)))
           "/bar" "/foo/bar"
           "http://example.com" "http://example.com"
           "https://example.com/bar" "https://example.com/bar"
           "bar" "bar"
           "../bar" "../bar"
           "//example.com/bar" "//example.com/bar")))
     (testing "with base URL for root context"
       (with-base-url "/"
         (are [obj expected]
             (= expected (u/to-str (u/to-uri obj)))
           "/bar" "/bar"
           "http://example.com" "http://example.com"
           "https://example.com/bar" "https://example.com/bar"
           "bar" "bar"
           "../bar" "../bar"
           "//example.com/bar" "//example.com/bar")))
     (testing "with base URL containing trailing slash"
       (with-base-url "/foo/"
         (are [obj expected]
             (= expected (u/to-str (u/to-uri obj)))
           "/bar" "/foo/bar"
           "http://example.com" "http://example.com"
           "https://example.com/bar" "https://example.com/bar"
           "bar" "bar"
           "../bar" "../bar"
           "//example.com/bar" "//example.com/bar")))))
