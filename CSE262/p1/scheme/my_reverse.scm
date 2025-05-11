;; my_reverse: reverse a list without using the scheme `reverse` function
;;
;; Your implementation of this function can use special forms and standard
;; functions, such as `car`, `cdr`, `list`, `append`, and `if`, but it cannot
;; use the built-in `reverse` function.
;;
;; Your implementation should be recursive.  It does not need to be tail
;; recursive.

(define (my-reverse l)
  (if (null? l)       ;; base case: if list empty, return empty list
      '()
      (append (my-reverse (cdr l)) (list (car l))))) ;; recursively reverse list, starting with cdr to take care of last elements first then append first element with car. Use 'list' keyword to create a list from these elements 