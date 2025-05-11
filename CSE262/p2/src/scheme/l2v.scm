;; list2vector takes a list and returns a vector, without using `list->vector`
(define (list2vector lst)  
  (let ((vec (make-vector (length lst))))  ; create a vector with same length as lst
    (define (fill-vector lst index)
      (if (null? lst)
          vec  ; return the filled vector when done/base case 
          (begin
            (vector-set! vec index (car lst))  ; set current element in vector
            (fill-vector (cdr lst) (+ index 1)))))  ; recursive call with remaining list
    (fill-vector lst 0)))  ; start recursion at index 0