;; prime_divisors: compute the prime factorization of a number
;;
;; To test this function, open a new `gsi` instance and then type:
;;  (load "prime_divisors.scm")
;; Then you can issue commands such as:
;;  (prime-divisors 60)
;; And you should see results of the form:
;;  (2 2 3 5)

;; This is a skeleton for the prime-divisors function.  For now, it just
;; returns #f (false)
;;
;; Note that you will almost certainly want to write some helper functions,
;; and also that this will probably need to be a recursive function.  You are
;; not required to use good information hiding.  That is, you may `define`
;; other functions in the global namespace and use them from
;; `prime-divisors`.
(define (prime-divisors n)
  ;; helper function to check if a number is divisible by a given prime
  (define (divisible? n p)
    (= (remainder n p) 0))

  ;; helper function to find the next divisor
  (define (next-divisor start)
    (define (is-prime? x)
      (define (check d)
        (cond ((>= (* d d) x) #t)
              ((divisible? x d) #f)
              (else (check (+ d 1)))))
      (check 2))
    (define (find-next p)
      (if (is-prime? p)
          p
          (find-next (+ p 1))))
    (find-next start))

  ;; recursively compute prime divisors
  (define (factorize n start)
    (cond ((<= n 1) '())  ;; base case: return empty list for n <= 1
          ((divisible? n start) 
           (cons start (factorize (/ n start) start)))  ;; divide and recurse with the same divisor
          (else (factorize n (next-divisor (+ start 1))))))  ;; try next possible divisor

  ;; start the factorization process with the first prime number, 2
  (factorize n 2))
