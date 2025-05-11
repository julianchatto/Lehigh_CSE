# CSE 262 Assignment 1

The purpose of this assignment is to ensure that you are familiar with the
three main programming languages that we will use in this class: Java,
Python, and Scheme.  Among the goals of this assignment are:

* To make sure you have a proper development environment for using these
  languages
* To introduce you to these languages, if you haven't used them before
* To introduce you to some features of these languages that you may not have
  seen before
* To get you thinking about how to program idiomatically

## Parts of the Assignment

This assignment has *four* parts, which are contained in three sub-folders:
`java`, `python`, and `scheme`.  Three tasks are similar: in Java, Python, and
Scheme, you will implement five "programs":

* `read_list` -- Read a list of values from stdin and put them in a list
* `reverse` -- Reverse a list, without using any built-in list functions
* `map` -- Apply a function to all elements of a list, without using any
  built-in map functions
* `tree` -- Implement a binary tree
* `prime_divisors`-- Factor an integer into its prime divisors

The README file in each sub-folder has some more information about programming
in each of these languages.

The *fourth* part of the assignment is to answer the questions at the end of
this file.

## Development Environments

I strongly encourage you to use Visual Studio Code as your development
environment.  It has good plug in support for Java and Python, and reasonable
support for Scheme.  This support is not just syntax highlighting, but also
code formatting, refactoring, code completion, and tooltips.  It will help
you to write better code in less time.

Remember that you are not allowed to use AI coding assistants, so please be
sure to disable Copilot when working in Visual Studio Code.

VSCode also has two very important features for this assignment: VSCode Remote
and Live Share.  If you do not want to install Java, Python, and Scheme on your
computer, you can use the sunlab, and with VSCode Remote, you can use VSCode to
connect to sunlab.  It's very nice.  If you choose to work in a team of two,
Live Share will make it much easier to pair program.

## Teaming

You may work in teams of two for this assignment.  If you choose to work in a
team, you should **pair program**.  You should not split the assignment.  You
will not be able to succeed in this class if you do not understand everything in
this assignment.  Furthermore, if you split the work, you and your teammate will
wind up having to solve the same hard problems, which means you'll do 100% of
the work for each step.  In contrast, if you pair program, things you figure out
in Java won't need to be re-learned in Python, so you'll do only about 50% of
the work for Python... that savings adds up!

If you wish to work in a team, you must email Prof Spear <spear@lehigh.edu>
by 11:59 PM on January 24, 2025.  Your email must follow these rules:

1. You must cc your project partner, so that I know that both team members
   are aware of the team request.
2. You must tell me which team member's repository you will be working in.

I will change the permissions on that repository, so that both students can
read and write to it.  You will not need to submit the assignment twice.

## Documentation

You are **required** to follow the documentation instructions that accompany
each part of the assignment.  Correct code that does not have documentation
will not receive full points.

**DO NOT FORGET THE QUESTIONS AT THE END OF THIS FILE**

## Deadlines

This assignment is due by 11:59 PM on Friday, February 7th.  You should have
received this assignment by using `git` to `clone` a repository onto the
machine where you like to work.  You can use `git add`, `git commit`, and
`git push` to submit your work.

You are strongly encouraged to proceed *incrementally*: as you finish parts of
the assignment, `commit` and `push` them.

## Start Early

You should not wait until the last minute to start this assignment.  Start
early, and stop often.  This strategy will maximize your learning and
minimize your stress.  I promise.

## Questions

Please be sure to answer all of the following 17 questions by writing responses
in this document.  Remember that these questions are worth 25% of your grade,
so please be sure to give complete and detailed answers.

### Read List

* Did you run into any trouble using `let` in Scheme?  Why?

'Let' is used to introduce a local variable in the program, which is a normal thing for it to do in scheme so we didn't run into any trouble using it but we had to keep in mind that x is only defined in the "read" function since it is a local variable to that function so we had to make sure we weren't trying to access x outside of that function. 
* What happens if the user enters several values on one line?

If you add serveral vlaues on one line, they get treated as the same element in a list. A new element is only added to the list if it is inputted on a new line. Spaces do not make strings get treated as different elements, only new lines do. Consequently, an item in the list can consist of multiple words and spaces. 
* What happens if the user enters non-integer values?

Non-integer values get treated the same as integer values in the sense that they will be considered part of one element of the list if they are all typed on the same line and different elements if they are entered on different lines. Multiple words can be in the same element and so can spaces, characters, and integers as long as they were all typed on the same line. 
* Contrast your experience solving this problem in Java, Python, and Scheme.

Scheme used a recursive approach with a helper function to accumulate values recursively and worked with linked lists as opposed to mutable arrays. Python follows an iterative approach and uses a while loop instead of recursion to append the values to a mutable list. EOFError is caught with try-except in Python while Scheme uses explicit EOF handling with "(eof-object? x)". Java implements a read function like in scheme and a while loop like python as opposed to the scheme recursion. It uses an ArrayList that is mutable and calls MyReverse<T> to use the reverse function we made. Java is the most verbose of the three. 

### Reverse

* What is tail recursion?

Tail recursion is a recursive function where the recursive call is the last statement that is executed by the function. There is essentially nothing left to execute after the recursive call so the compiler can optimize the recursion by avoiding additional stack frames and turning it into an iterative process. 
* Is your code tail recursive?

Both the Java and Python implementations use iteration and a for loop so they are not tail recursive. The Scheme implementation is also not tail recursive because the recursive call to my-reverse happens inside append so append must wait for my-reverse to return before it executes, making stack frames accumulate. There is recursion but it is not in tail position since additional work (append) is done after the recursive call.
* How would you write a test to see if Scheme is applying tail recursion
  optimizations?

We would run my-reverse with a large input list to see if there is a stack overflow. If it is not optimized, it will cause a stack overflow due to the excessive function calls. If it is optimized, the same stack frame would be reused and there would not be overflow. 
* Contrast your experience solving this problem in Java, Python, and Scheme.

For Java and Python, we used a for loop to append to a list while in Scheme we used recursion with car and cdr. Java made use of generics and collections (List<T>) and used the .get() method to get the items to insert into the reversed list while in Python we just initialized a new list to insert into at index 0. Scheme we used recursion but not tail recursion and included a base case to prevent errors and being stuck in an infinite loop. 

### Map

* What kinds of values can be in `l`?

'l' can be any iterable for Python or any sequence of objects that supports iteration. Each element of the sequnece can be any type (int, string, float, etc.). Similarly, in Scheme 'l' can be any list of any type. In Java, 'l' is represented as a List<T>, where T can be any type. T is generic, so the list can hold elements of any type (ints, strings, or user-defined types). 

* What are the arguments to the function `func`?

In Python, func takes each element of the list l as its argument. For each iteration, func(item) applies the function func to the current element of the list. In Scheme, func takes a single element or multiple elements of the list as an argument. 'car l' represents the current element and 'cdr l' represents the rest of the list. In Java, func is a Function<T, T>, so accepts a single argument of type T (the element of the list) and returns a result of type T. 

* Why is this function built into scheme when it's so simple to write?

It is built in to avoid repetitive code and support Scheme's commitment to functional style, abstraction, and concise code. Map allows users to process lists efficiently without needing to manually iterate through elements.
* Contrast your experience solving this problem in Java, Python, and Scheme.

We solved this problem in Java and Python using iteration because it is a straightforward way to apply func to every item in the list. In Scheme, we used recursion to do this since Scheme's car and cdr keywords make this easy to do. We stored the new list in an ArrayList for Java since ArrayLists can have their size modified and support the add() function. We used a list in Python because they allow duplciates and variables of multiple types, and they support the append() function to add new items after func has been applied. In Scheme were used a list to allow for efficient access to the first element (car) and the rest of the list (cdr).

### Tree

* How do you feel about closures versus objects?  Why?

Java and Python used objects while Scheme used closures. Closures enfore encapsulation, which is good for a more functional and concise program but may be harder to interpret and find the state of the program. Objects are more structured and more intuitive (having left and right children explicity defined, etc.), which is why the Java and Python programs were easier for us to grasp at first. The Scheme program used closures which made debugging it harder since it was harder to understand the states of the functions and how to refer to the left and right children in order to understand the structure of the tree so we could write methods to support it. 
* How do you feel about defining a tree node as a generic triple?

Generic triples are more type-safe in Java so we think they are appropriate for tree nodes but in Python they are not type-safe so they would be worse for defining tree nodes. An alternative in Python would be to use a custom class so that only that specific class type could be used to define a tree node. 


* Contrast your experience solving this problem in Java, Python, and Scheme.

In all three, clearing the tree consisted of simply setting the root to null/empty. For Scheme, we used the car and cdr functions for recursion in the helper functions. Helper functions were also used for insert, display, inorder, and preorder in Python, and for inorder and preorder in Java. Each Node had a value, left child, and right child in Java and Python while in Scheme the nodes just had a value (x). 

### Prime Divisors

* Why did you choose the Scheme constructs that you chose in order to solve this problem? 

We chose these constructs in order to make the solution recursive, and helper functions were included to check if a number is divisible by a given prime in order to make the overall code more concise and functional. We used base case handling to  handle cases when the number is less than 2 and has no prime factorization. We also skipped numbers that were even after checking 2 because 2 is the only possible even prime factor. 
* Contrast your experience solving this problem in Java, Python, and Scheme.

In Scheme, we used helper functions and our final solution consisted of multiple functions to build a recursive solution while our Java and Python code used iteration and not recursion. Java and Python solutions also only consisted of a single method, which had while/for loops. All three languages checked base cases and skipped even divsiors besides 2 in order to increase efficiency. 