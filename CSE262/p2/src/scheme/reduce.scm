;; reduce takes a binary function, a list, and an identity value, and computes
;; the result of repeatedly applying that function
;;
;; Example: (reduce + '(1 2 3) 0) ==> 6
;;
;; Example: (reduce * '() 1) ==> 1
(define (reduce op l identity)
     (if (null? l)
      identity ;; base case, just return identity
      (op (car l) (reduce op (cdr l) identity)))) ;; recursive part, apply function to cdr of list and use operator to apply it with car of the list