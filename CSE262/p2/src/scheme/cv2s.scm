;; charvec2string takes a vector of characters and returns a string
(define (charvec2string cv)
    (let ((str (make-string (vector-length cv))))  ; create empty string with same length as cv
    (define (fill index)
      (if (= index (vector-length cv))
          str  ; return the final string when all characters are set/base case 
          (begin
            (string-set! str index (vector-ref cv index))  ; copy char from vector to string
            (fill (+ index 1)))))  ; recursive call to the next index
    (fill 0)))  ; start filling from index 0