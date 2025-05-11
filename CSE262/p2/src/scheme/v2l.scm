;; vector2list takes a vector and returns a list, without using `vector->list`
(define (vector2list vec)
  (define (helper vec index)
    (if (= index (vector-length vec))
        '()  ; base case, return empty list when index reaches the length
        (cons (vector-ref vec index) (helper vec (+ index 1))))) ;  recursively convert vector to list by iterating through vector elements and cons them to new list

  (helper vec 0))  ; Start from index 0