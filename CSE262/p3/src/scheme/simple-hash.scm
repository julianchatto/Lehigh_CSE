;; simple-hash creates a basic hash *set* of strings.  It uses the "method
;; receiver" style that you have seen previously: `make-hash` returns a function
;; that closes over some state; that function takes two arguments (a symbol and
;; a string).
;;
;; The argument to make-hash is a number: it should be positive.  It will be the
;; size of the "bucket vector" for your hash table.
;;
;; Your hash table should be a vector of lists.  Three operations can be
;; requested:
;; - 'contains string - Returns true if the string is in the hash set, false
;;   otherwise
;; - 'insert string - Returns true if the string was inserted into the hash set,
;;   false if it was alread there.
;; - 'remove string - Returns true if the string was removed from the hash set,
;;   false if it was not present to begin with.
;;
;; Here's an example execution:
;; (define my-hash (make-hash 32))
;; (my-hash 'insert "hello") <-- returns true
;; (my-hash 'contains "world") <-- returns false
;; (my-hash 'contains "hello") <-- returns true
;; (my-hash 'insert "hello") <-- returns false
;; (my-hash 'remove "world") <-- returns false
;; (my-hash 'remove "hello") <-- returns true
;; (my-hash 'remove "hello") <-- returns false
;; (my-hash 'contains "hello") <-- returns false
;;
;; To "hash" input strings, you should use the (very simple) djb2 function from
;; <http://www.cse.yorku.ca/~oz/hash.html>


;; TODO: implement this function
(define (djb2-hash str) ;; defines djb2 hash function 
  (define (add hash chars) ;; inner helper function with current hash value and remaining characters of str
    (if (null? chars)
        hash ;; return hash value if no chars left 
        (add (+ (* 33 hash) (char->integer (car chars))) (cdr chars)))) ;; otherwise update hash value and call "add" recursively with updated hash and remaining chars (cdr chars)
  (add 5381 (string->list str))) ;; convert input to list of chars and add to initial hash value 

(define (make-hash size) ;; size determine bucket number
  (let ((buckets (make-vector size '()))) ;; each bucket orignally empty (empty list)
    (lambda (op str) ;; lambda for operation to be done on a str
      (let* ((index (remainder (djb2-hash str) size)) ;; compute hash and bucket (index) is hash mod size of table 
             (bucket (vector-ref buckets index))) ;; get bucket based on index 
        (cond
          ((eq? op 'contains)
           (member str bucket)) ;; contains checks if str is in a bucket using member 
          ((eq? op 'insert)
           (if (member str bucket) ;; cannot insert if already in bucket, return false 
               #f
               (begin
                 (vector-set! buckets index (cons str bucket)) ;; add str to bucket if not already in bucket 
                 #t)))
          ((eq? op 'remove)
           (if (member str bucket) ;; only remove if member of bucket 
               (begin
                 (vector-set! buckets index (remove (lambda (x) (equal? x str)) bucket)) ;; removes str from bucket using equal function
                 #t)
               #f)))))))
