;; Compute the exclusive or of two values, using only and, or, and not
;;
;; xor should always return a boolean value
(define (xor a b)
  (and (or a b) (not (and a b)))) ;; and, or, and not return boolean values in Scheme
                                ;; if a and b the same return #f, which is done via the not operator 
                                ;; outer "and" ensures two true values will not return true overall 