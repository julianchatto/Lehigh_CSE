;; contains-substring checks if a string contains the given substring.  It does
;; not count how many times: it merely returns true or false.
;;
;; The first argument to contains-substring is the string to search
;; The second argument to contains-substring is the substring to try and fine
;;
;; Here's an example execution: 
;; (contains-substring "hello" "ello") <-- returns true
;; (contains-substring "hello" "yell") <-- returns false
;; (contains-substring "The quick brown fox jumps over lazy dogs" "ox") <-- returns true
;;
;; You should implement this on your own, by comparing one character at a time,
;; and should not use any string comparison functions that are provided by gsi.

;; TODO: implement this function
(define (contains-substring source pattern)
    (define (start s p) ;; check if starting characters match 
    (cond
      ((null? p) #t)  ;; empty pattern always matches
      ((null? s) #f)  ;; nothing left in source before pattern found
      ((eq? (car s) (car p)) (start (cdr s) (cdr p))) ;; if two characters match, check next two characters
      (else #f)))
  
  (define (search s p)
    (cond
      ((null? s) #f)  ;; exhausted source and did not find pattern
      ((start s p) #t)  ;; found pattern at current position
      (else (search (cdr s) p))))  ;; recur search on rest of source to try and find pattern
  
  (search (string->list source) (string->list pattern)) ;; convert both source and pattern to list for easy comparison using car and cdr
)