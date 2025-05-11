;; three-zip takes three lists, and returns a single list, where each entry in
;; the returned list is a list with three elements.
;;
;; The nth element of the returned list is a list containing the nth element of
;; the first list, the nth element of the second list, and the nth element of
;; the third list.
;;
;; Your implementation should be tail recursive.
;;
;; If the three lists do not have the same length, then your code should behave
;; as if all of the lists were as long as the longest list, by replicating the
;; last element of each of the short lists.
;;
;; Example: (three-zip '(1 2 3) '("hi" "bye" "hello") '(a b c))
;;          -> ('(1 "hi" a) '(2 "bye" b) '(3 "hello" c))
;;
;; Example: (three-zip '(1 2 3 4) '("hi" "bye" "hello") '(a b c))
;;          -> ('(1 "hi" a) '(2 "bye" b) '(3 "hello" c) '(4 "hello" c))
(define (three-zip l1 l2 l3)
     (define (extend lst new-length) ;; extend list to whatever max list length is 
        (if (>= (length lst) new-length)
            lst ;; do not extend, return original list 
            (append lst (make-list (- new-length (length lst)) (last lst))))) ;; extend and return extended list using last element

  (define (last lst)
    (if (null? (cdr lst)) (car lst) (last (cdr lst)))) ;; recursively get last element, if only one element in list use car to return just that element

  (define (helper lst1 lst2 lst3 result)
    (if (null? lst1) 
        (reverse result)  ;; reverse to preserve correct order at the end (base case)
        (helper (cdr lst1) (cdr lst2) (cdr lst3) ;; recursively combine elements to make new list
                (cons (list (car lst1) (car lst2) (car lst3)) result))))

  (let* ((max-len (max (length l1) (length l2) (length l3)))
         (ext-l1 (extend l1 max-len))
         (ext-l2 (extend l2 max-len))
         (ext-l3 (extend l3 max-len)))
    (helper ext-l1 ext-l2 ext-l3 '())))