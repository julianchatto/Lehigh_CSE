(define capture (lambda (x) (lambda (y) (and x y))))
(define passing (capture #t))
(passing #f)
