; gcd: compute the greatest common divisor of two values x and y
;
; To test this function, open a new `gsi` instance and then type:
;  (load "gcd.scm")
; Then you can issue commands such as:
;  (gcd 6 2)
; And you should see results of the form:
;  2

; This method checks first checks to see if either number is 0. If so, returns 0.
; Then it checks if they are equal. If so, then returns one of the numbers
; Then it checks to see which one is greater than the other. The smaller number
; is subtracted from the larger and gcd is called again. This happens until the numbers
; are the same size

(define (gcd x y)
  (cond ((= x 0) 0)                           ; if x or y is 0 then the gcd will be 0
        ((= y 0) 0)
        ((= x y) x)                           ; when the numbers are equal, the gcd is found
        ((> x y) (gcd (- x y) y))             ; if x or y is larger, decrement the larger one by the 
        ((> y x) (gcd x (- y x)))))      ; smaller one and call the function again     

(gcd 30 27)
; Writing in Scheme was challenging. It is unlike any other language I have written,
; so it took some getting used to. I chose cond for a construct. I used it as an 
; if else statement from Java. They have super different syntax, but provide the
; same functionality
