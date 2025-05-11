(define both-true (lambda (x y) (cond ((and x y) 'both) (x 'justx) (y 'justy) (#t #f))))
(both-true #f 1)
