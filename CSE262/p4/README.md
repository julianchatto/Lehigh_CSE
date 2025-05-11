# CSE 262: Programming Assignment 4: Interpreting "slang"

In this assignment, you will complete the last stage of the "Slang"
interpreter, by building a tree-walk interpreter.  This will let you execute
programs for the relatively complete subset of the Slang programming language
that we have been working with in the previous assignments.  In order to gain
experience working in new languages, you will implement the interpreter
twice, once in Java, and then in Python.

## Project Details

Interpreting is not hard, once you get the hang of what it entails.  The hard
part is creating a standard library of useful functions.  One way to think
about it is like this: as you walk the AST, many nodes just need to return
themselves, and many of the rest just need to do somewhat "standard"
computation.  However, when there is an Call of a function to its arguments,
if that is a built-in function, we need to have some code to run.
Furthermore, that code needs to be very smart: it needs to check the types of
its arguments, check the number of arguments, and only operate if it is
reasonable to do so.

An additional point of interest here is that your interpreter needs to know
when to interpret, and when not.  For example, for a Call node, the rule is
"first, interpret the first expression to get the function.  Then interpret
the other expressions to get its arguments.  Then type-check.  Then run the
function and return the result."

As you work on this assignment, you should try to write *idiomatic* code.
Try, as much as possible, to use language features that make your code more
readable, more familiar to experts, and more succinct and expressive.

The assignment does *not* assume that you have a working p3.  The interpreter
will take as its input the *output* of the parser.  We are providing this for
you.

## What To Expect

The act of walking the AST to interpret is relatively straightforward.  The
Python code (slang_evaluator.py) is only 100 lines, and the Java code is only
145 lines.  The harder part is the standard library.  Types in Java make this
*much* harder.  In Python, the standard library is under 400 lines of code,
with comments.  In Java, it's more like 700 lines.  This code uses a
functional style, which helps a lot to keep the line count low.  If you
aren't using functional idioms, and you aren't writing helper functions to
encapsulate common behaviors, your Java code will easily reach 2000 lines.
Be sure to use your language well!

Because we are implementing the interpreter in Java and Python, some things
are easy.  For example, we don't need to write our own garbage collector,
because the interpreter has a garbage collector already.  Similarly, you
should feel free to use existing functions from Java and Python (like `cos`).

You should also think carefully about the role that linked environments can
play in emulating the global scope and scopes for stack frames.  It's
actually quite elegant.  You will hopefully find tremendous satisfaction from
discovering that you can pass functions as arguments, you can capture
variables with lambdas, you can create arbitrarily large data structures on
the heap, and you can generally do most of the fancy stuff that a real
programming language allows.

Of course, to know if your code is correct, you'll also want Scheme programs
that exercise your interpreter.  We provide a few simple ones, and you should
feel free to create others as you see fit.

Rather than work on Java then Python, or vice versa, it is probably good to
get both to do a few math functions, and then start expanding the two
implementations simultaneously.  It is very satisfying to get a basic
"calculator" working, and once you do, most of the rest of the code will be
pretty easy to figure out.

## Tips and Reminders

**Start Early**.  Just reading the code and understanding what is happening
takes time.  If you start reading the assignment early, you'll give yourself
time to think about what is supposed to be happening, and that will help you
to figure out what you will need to do.

As always, please be careful about not committing unnecessary files into your
repository.

## Grading

The Java and Python code will each be worth 50% for this assignment.  Please
be sure to read `specifications/StdLib.md` for notes about the standard
library functions you should implement.

You should be sure to comment your code.  For Java and Python, comments will
not be graded extensively, only if their absence is egregious.  You may wish
to use the provided code as a reference for how I comment code, and mimic it.
In particular, note that good comments are formatted in a way that lets the
IDE read them and build tooltips for you from them.

Please be sure to use your tools well.  For example, Visual Studio Code (and
emacs, and vim) have features that auto-format your code.  You should use
these features, so that your code is legible.

Note that some parts of the assignment, like error messages, are
intentionally vague: you need to start thinking about what it means for code
to be "correct".  There are many criteria that cannot be established through
unit testing.

## Extra Credit

In response to the student survey, the following portions of the assignment
are optional / extra credit:

- The portions of the standard library relating to vectors.  Completing these
  tests will be worth 5 points of extra credit for each language.

The following portions of the assignment will not be evaluated at all:

- The portions of the math standard library that are not covered by the
  provided unit tests
  
In addition, please note that there is a "tail recursion" test that is
possible to pass in Python, but not in Java.  If you figure this out, you'll
get 5 points of extra credit on the Python code.

## Collaboration and Getting Help

Students may work in teams of 2 for this assignment.  **I strongly recommend
working with a partner.**  If you plan to change your teammate, please notify
me early, so that I can set up your repository access.  If you are working in
a team, you should **pair program** for the entire assignment. After all,
your goal is to learn together, not to each learn half the material.

If you require help, you may seek it from any of the following sources:

* The professor and TAs, via office hours or Piazza
* The Internet, as long as you use the Internet as a read-only resource and
  do not post requests for help to online sites.
* You should try not to use the Internet when working on the Scheme portion
  of the assignment.  I cannot enforce this requirement, but you'll learn an
  awful lot less if you look for Scheme answers online.

It is not appropriate to share code with past or current CSE 262 students,
unless you are sharing code with your teammate.

StackOverflow is a wonderful tool for professional software engineers.  It is
a horrible place to ask for help as a student.  You should feel free to use
StackOverflow, but only as a *read only* resource.  In this class, you should
**never** need to ask a question on StackOverflow.

You should not use any generative AI tools when working on this assignment.

## Deadline

You should be done with this assignment before 11:59 PM on May 9th, 2025.
Please be sure to `git commit` and `git push` before that time, so that I can
promptly collect and grade your work.

There are many parts to this assignment, so you will probably want to `git
push` frequently.

Please note that there will be no extensions for this assignment: it is due
as late as is possible for me to still assign course grades before the
deadline.
