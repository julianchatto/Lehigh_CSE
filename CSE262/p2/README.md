# CSE 262: Programming Assignment 2: Scanning "slang"

In the remaining three assignments of the semester, we will be building a
full interpreter for a relatively complete subset of the Scheme programming
language.  To avoid confusion, we will call our language "slang" (because it
is a Scheme-like LANGuage).

Slang will not be a proper subset of Scheme: as we move through the semester,
some features will be defined differently in slang than in Scheme, so that it
is easier to implement slang.  And, of course, some challenging features of
Scheme won't be part of our language.  Our goal is to be Turing complete, but
not a production-quality interpreter.

In the first phase of the assignment, we will only worry about *scanning*
slang.  That is, given a string that purports to be slang code, we will try
to turn it into a sequence of tokens.  Just like `gsi`, slang's interpreter
can either be given a file, or take input via a "read, evaluate, print loop"
(REPL).

Implementing a scanner for slang will provide an opportunity to learn more
about Scheme.  Implementing it twice, in two different languages, will
provide an opportunity to compare and contrast different programming
languages.  We'll also write some Scheme code as part of this assignment, in
order to get a deeper understanding of functional programming and Scheme
syntax.

## Project Details

Scanning is the first step in any compiler or interpreter.  It is the step
that turns source code into "tokens" that are easier to work with throughout
the rest of the compiler/interpreter.  Since the scanner in this assignment
is not connected to a parser, it will simply output the tokens.  Java code
for this output is provided.

As you work on this assignment, you should try to write *idiomatic* code.
Try, as much as possible, to use language features that make your code more
readable, more familiar to experts, and more succinct and expressive.

## What To Expect

The Java folder has the most starter code, and is probably the most
approachable part of the assignment.  It isn't "easy" to implement a scanner
for a regular language, but it isn't incredibly difficult either.  With just
a small amount of lookahead, you should be able to correctly recognize all of
the scheme syntax, and produce tokens.  Note that you should only need to
work on a single file: `Scanner.java`.  As you do, you should try to solve
this in a way that is familiar to a student who took CSE 017.  In particular,
this means you should probably have a separate method for processing each
scanner state.

After you've made progress on your Java code, you should turn to Python.
Your Python implementation should be *very different* from your Java
implementation: it should not use any user-defined types, instead using naked
`dict` objects to store all data.  Additionally, it should use a
*table-driven* scanner.  A constructor for a `transition` object (actually
just a `dict`) is provided, which should guide you.  Note that a table-driven
scanner is much more maintainable, but it requires you to think in a more
functional, less imperative way.

There is also a testing folder, which includes all of the tests that I will
run against your scanners.

The Scheme folder has a set of small problems that will encourage you to get
more familiar with Scheme syntax, and with some of the advanced behaviors
that we will need to implement in later phases of the assignment.

## Tips and Reminders

**Start Early**.  Just reading the code and understanding what is happening
takes time.  If you start reading the assignment early, you'll give yourself
time to think about what is supposed to be happening, and that will help you
to figure out what you will need to do.

When it comes time to implement your scanner, you will probably want to do the
Java code first.  It is up to you whether to try to implement a full scanner
first, and then test it, or to implement incrementally (for example, starting
with numbers, then characters, then expressions).  Strings and characters are
probably the most tricky.

As always, please be careful about not committing unnecessary files into your
repository.

## Grading

The Java code should be the most familiar, and will be weighted the heaviest: it
will be worth 50% of your grade on this assignment.  The Python code will be
worth 35%, and the Scheme code will be worth 15%.  Please be sure to read the
per-folder README.md files for more details.

You should be sure to comment your code.  For Java and Python, comments will not
be graded extensively, only if their absence is egregious.  You may wish to use
the provided code as a reference for how I comment code, and mimic it.  In
particular, note that good comments are formatted in a way that lets the IDE
read them and build tooltips for you from them.

Please be sure to use your tools well.  For example, Visual Studio Code (and
emacs, and vim) have features that auto-format your code.  You should use these
features, so that your code is legible.

Note that some parts of the assignment, like error messages, are intentionally
vague: you need to start thinking about what it means for code to be "correct".
There are many criteria that cannot be established through unit testing.

## Collaboration and Getting Help

Students may work in teams of 2 for this assignment.  **I strongly recommend
working with a partner.**  If you plan to work in a team, you must notify me
by February 14th, so that I can set up your repository access.  If you are
working in a team, you should **pair program** for the entire
assignment. After all, your goal is to learn together, not to each learn half
the material.

If you require help, you may seek it from any of the following sources:

* The professor and TAs, via office hours or Piazza
* The Internet, as long as you use the Internet as a read-only resource and do
  not post requests for help to online sites.
* You should try not to use the Internet when working on the Scheme portion of
  the assignment.  I cannot enforce this requirement, but you'll learn an awful
  lot less if you look for Scheme answers online.

It is not appropriate to share code with past or current CSE 262 students,
unless you are sharing code with your teammate.

StackOverflow is a wonderful tool for professional software engineers.  It is
a horrible place to ask for help as a student.  For the Java and Python parts
of the assignment, you should feel free to use StackOverflow, but only as a
*read only* resource.  In this class, you should **never** need to ask a
question on StackOverflow.

You should not use any generative AI tools when working on this assignment.

## Deadline

You should be done with this assignment before 11:59 PM on March 7th, 2025.
Please be sure to `git commit` and `git push` before that time, so that I can
promptly collect and grade your work.

There are many parts to this assignment, so you will probably want to `git
push` frequently.
