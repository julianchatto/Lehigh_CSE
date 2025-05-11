;; Compute the nand of two values, using only the `or` and `not` functions
;;
;; nand should always return a boolean value
(define (nand a b) 
    (not (and a b))) ;; nand is negation of "and" so just negate the and of a and b