;; my_map: apply a function to every element in a list, and return a list
;; that holds the results.
;;
;; Your implementation of this function is not allowed to use the built-in
;; `map` function.

(define (my-map func l)
  (if (null? l)               ;; base case: if list empty, return empty list
      '()
      (cons (func (car l))    ;; apply func to first element and cons it with recursive call
            (my-map func (cdr l)))) ;; recursively process rest of list using cdr
)  
