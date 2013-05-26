;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

;; Author: Gary Fredericks

(ns clojure.test-clojure.destructuring
  (:use clojure.test))

(deftest or-with-non-symbols-test
  (testing "keyword keys in the :or map throw an exception"
    (is (thrown? Exception
                 (eval '(fn picnic [{botulism :botulism
                                     :or {:botulism 6}}]
                          botulism)))))

  (testing "a non-map in the :or entry throws an exception"
    (is (thrown? Exception
                 (eval '(let [{:or 42} {}] :hallo))))))
