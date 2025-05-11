(define (sum-pos x) (if (= x 0) 0 (+ x (sum-pos (- x 1)))))
(sum-pos 10000)

