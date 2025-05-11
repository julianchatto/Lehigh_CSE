;; read_list: Use the `read` function to read from the keyboard and put the
;; results into a list.  The code should keep reading until EOF (control-d) is
;; input by the user.  It should use recursion, not iterative constructs.
;;
;; The order of elements in the list returned by (read-list) should the reverse
;; of the order in which they were entered.
;;
;; You should *not* define any other functions in the global namespace.  You may
;; need a helper function, but if you do, you should define it so that it is
;; local to `read-list`.

(define (read-list)
  (define (helper lst) ;; lst holds all the values that have been read so far
    (let ((x (read))) ;; bind innput to x
      (if (null? x) ;; check if we have reached the end of the list
          lst ;; return
          (helper (cons x lst)) ;; return and call helper again
      )
    )
  )
  (helper '())
)
