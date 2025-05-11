;; tree: A binary tree, implemented as a "closure"
;;
;; The tree should support the following methods:
;; - 'ins x      - Insert the value x into the tree
;; - 'clear      - Reset the tree to empty
;; - 'inslist l  - Insert all the elements from list `l` into the tree
;; - 'display    - Use `display` to print the tree
;; - 'inorder f  - Traverse the tree using an in-order traversal, applying
;;                 function `f` to the value in each non-null position
;; - 'preorder f - Traverse the tree using a pre-order traversal, applying
;;                 function `f` to the value in each non-null position
;;
;; Note: every method should take two arguments (the method name and a
;; parameter).  If a method is defined as not using any parameters, you
;; should still require a parameter, but your code can ignore it.
;;
;; Note: You should implement the tree as a closure.  One of the simplest
;; examples of a closure that acts like an object is the following:
;;
;; (define (make-my-ds)
;;   (let ((x '())) (lambda (msg arg)
;;       (cond ((eq? msg 'set) (set! x arg) 'ok) ((eq? msg 'get) x) (else 'error)))))
;;
;; In that example, I have intentionally *not* commented anything.  You will
;; need to figure out what is going on there.  If it helps, consider the
;; following sequence:
;;
;; (define ds (make-bst))   ; returns nothing
;; (ds 'get 'empty)         ; returns '()
;; (ds 'set 0)              ; returns 'ok
;; (ds 'get 'empty)         ; returns 0
;; (ds 'do 3)               ; returns 'error
;;
;; For full points, your implementation should be *clean*.  That is, the only
;; global symbol exported by this file should be the `make-bst` function.

(define (make-bst)
  (let ((root '()))

    ;; helper to create a new node (node class)
    (define (make-node left value right)
      (list left value right))

    (define (insert x)
      (define (insert-helper node x)
        (if (null? node)
            ;; when node is empty
            (make-node '() x '())
            ;; when node is not empty, need to decide which subtree to insert
            (if (< x (cadr node))
                ;; insert to left
                (make-node (insert-helper (car node) x) (cadr node) (caddr node))
                ;; insert to right
                (make-node (car node) (cadr node) (insert-helper (caddr node) x)))))
      (set! root (insert-helper root x)))

    ;; clear tree
    (define (clear)
      (set! root '()))

    ;; insert a list of elements into the tree 
    (define (inslist l)
      (for-each insert l))

    ;; display tree using in-order traversal
    (define (display-tree)
      (define (display-helper node)
        (if (null? node)
            '()
            (begin
              (display-helper (car node)) ;; left subtree
              (display (cadr node)) ;; value
              (display " ") ;; space to make it look pretty
              (display-helper (caddr node))))) ;; right subtree
      (display-helper root))

    ;; in-orderly traverse appying f
    (define (inorder f)
      (define (inorder-helper node)
        (if (null? node)
            '()
            (begin
              (inorder-helper (car node))
              (f (cadr node))
              (inorder-helper (caddr node)))))
      (inorder-helper root))

    ;; pre-orderly traverse applying f
    (define (preorder f)
      (define (preorder-helper node)
        (if (null? node)
            '()
            (begin
              (f (cadr node))
              (preorder-helper (car node))
              (preorder-helper (caddr node)))))
      (preorder-helper root))

    ;; return the "object" as a closure which responds to method messages.
    (lambda (msg arg)
      (cond ((eq? msg 'ins) (insert arg)) ;; insert a single value
            ((eq? msg 'clear) (clear)) ;; clear the tree
            ((eq? msg 'inslist) (inslist arg)) ;; insert a list of values
            ((eq? msg 'display) (display-tree)) ;; display the tree
            ((eq? msg 'inorder) (inorder arg)) ;; in-order traversal
            ((eq? msg 'preorder) (preorder arg)) ;; pre-order traversal
            (else 'error)))))
