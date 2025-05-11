(define (prime-divisors n) (cond ((< n 2) '()) (#t (cons (factor n 2) (prime-divisors (/ n (factor n 2)))))))

(define (factor x y) (cond ((> y x) 0) ((= (% x y) 0) y)(#t (factor x (+ y 1)))))

(prime-divisors 50)

; I enjoyed writing this code more than the gcd program. I still had to write the
; program in Java first, but I am more confident now. I used cond and cons in this
; program. Cons is used to append recurrsive lists together. In java I would have
; just used a global array. Cond is the if else statement of Scheme. I also had to
; choose to use remainder because modulo is weird in Scheme (aka I don't know how
; to use it lol)
