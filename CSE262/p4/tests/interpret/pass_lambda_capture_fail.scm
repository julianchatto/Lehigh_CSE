(define capture (lambda (x) (lambda (y) (and x y))))
(define failing (capture #f))
(failing #t)
