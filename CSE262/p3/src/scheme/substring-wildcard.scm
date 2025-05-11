;; substring-wildcard is like contains-substring, but it understands
;; single-character wildcards in the pattern string.  Wildcards are represented
;; by the ? character.  Note that this is a slightly broken way of doing
;; wildcards: the '?' character cannot be matched exactly.
;;
;; Here's an example execution: 
;; (contains-substring "hello" "e?lo") <-- returns true
;; (contains-substring "hello" "yell") <-- returns false
;; (contains-substring "The quick brown fox jumps over lazy dogs" "q?ick") <-- returns true
;;
;; You should implement this on your own, by comparing one character at a time,
;; and should not use any string comparison functions that are provided by gsi.

;; TODO: implement this function
(define (contains-substring source pattern) ;; do same as contains-substring but consider wildcard
  (define (start s p)
    (cond
      ((null? p) #t)  ;; empty pattern always matches
      ((null? s) #f)  ;; nothing left in source before pattern found
      ((or (eq? (car s) (car p)) (eq? (car p) #\?)) ;; use character literal for ? to have it match any character 
       (start (cdr s) (cdr p))) ;; match character or wildcard, check next
      (else #f)))

  (define (search s p)
    (cond
      ((null? s) #f)  ;; exhausted source and did not find pattern
      ((start s p) #t)  ;; found pattern at current position
      (else (search (cdr s) p))))  ;; recur search on rest of source
  
  (search (string->list source) (string->list pattern))) ;; convert both source and pattern to list