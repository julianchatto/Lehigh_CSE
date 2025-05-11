(define (sum-pos-accum x y) (if (= x 0) y (sum-pos-accum (- x 1) (+ x y))))
(sum-pos-accum 10000 0)
