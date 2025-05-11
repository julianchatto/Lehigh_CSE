;; Compute the nor of two values, using only the `and` and `not` functions
;;
;; nor should always return a boolean value
(define (nor a b)
    (not (or a b))) ;; nor returns opposite of "or" so just negate the or of a and b