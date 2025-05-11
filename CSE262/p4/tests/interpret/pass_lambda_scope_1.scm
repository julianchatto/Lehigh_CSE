(define myvar 10)
(define myfunc (lambda (myvar) (lambda () myvar)))
(define scopetest (myfunc 11))
(scopetest)
