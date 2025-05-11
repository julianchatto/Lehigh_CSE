# CSE 262 Assignment 1: Scheme

In this part of the assignment, you will write our five programs in
Scheme. The "Gambit" Scheme interpreter is available on the SunLab as `gsi`,
or you can install it on your local machine.  Note that you must type `module
load gambit` before `gsi` will be available on SunLab machines..

Scheme's syntax is radically different than Java and Python, as is its
philosophy: You should think about recursion as much as possible!  You should
read Chapter 11 of the textbook to get some ideas about how to think like a
Scheme programmer and write idiomatic code.

## Building and Running the Code

Since `gsi` is an interpreter, you may find it hard to figure out how to run
and test your code.  I typically use the following process:

1. Write your code in the appropriate file
2. In `gsi`, type `(load "filename.scm")` to load your file
3. Then invoke your code to test it (e.g., `(define x (read-list)) (my-reverse x)`)

When you find bugs, you will probably need to exit your interpreter, fix the
code, and restart the interpreter.  You can use `ctrl-d` to exit the
interpreter.

## Turn-In Details

You should do all of your work for this part of the assignment in the five
`.scm` files in this folder.  When you have something worth turning in, you
can use a variation on this sequence of commands:

```bash
git add read_list.scm
git commit -m "<your commit message here>"
git push
```

## Documentation Details

In each file, you should comment your code extensively, to describe what it
is doing.  This will be graded.  You should write your comments assuming that
the audience is someone who has never programmed in Scheme before.  Be
verbose!
