(ns clojure.test-clojure.transients
  (:use clojure.test))

(deftest popping-off
  (testing "across a node boundary"
    (are [n] 
      (let [v (-> (range n) vec)]
        (= (subvec v 0 (- n 2)) (-> v transient pop! pop! persistent!)))
      33 (+ 32 (inc (* 32 32))) (+ 32 (inc (* 32 32 32)))))
  (testing "off the end"
    (is (thrown-with-msg? IllegalStateException #"Can't pop empty vector"
          (-> [] transient pop!))))
  (testing "copying array from a non-editable when put in tail position")
    (is (= 31 (let [pv (vec (range 34))]
                (-> pv transient pop! pop! pop! (conj! 42))
                (nth pv 31)))))

(defn- hash-obj [hash]
  (reify Object (hashCode [this] hash)))

(deftest dissocing
  (testing "dissocing colliding keys"
    (is (= [0 {}] (let [ks (concat (range 7) [(hash-obj 42) (hash-obj 42)])
                        m (zipmap ks ks)
                        dm (persistent! (reduce dissoc! (transient m) (keys m)))]
                    [(count dm) dm])))))

(deftest test-disj!
  (testing "disjoin multiple items in one call"
    (is (= #{5 20} (-> #{5 10 15 20} transient (disj! 10 15) persistent!)))))

(deftest empty-transient
  (is (= false (.contains (transient #{}) :bogus-key))))

(deftest subvec-transient
  (testing "basic operations work"
    (is (= [2 3 42]
           (-> (range 10)
               (vec)
               (subvec 2 5)
               (transient)
               (pop!)
               (conj! 42)
               (persistent!)))))
  (testing "pop throws on empty vector"
    (is (thrown? IllegalStateException
                 (-> (range 10)
                     (vec)
                     (subvec 3 5)
                     (transient)
                     (pop!)
                     (pop!)
                     (pop!)))))
  (testing "thread-local access enforcement"
    (let [v @(future (-> (range 99)
                         (vec)
                         (subvec 78 90)
                         (transient)))]
      (is (thrown? IllegalAccessError
                   (nth v 3))))))
