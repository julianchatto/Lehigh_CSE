# CSE 262: Programming Assignment 3: Python

In this folder, you should implement a parser for slang in Python.  You should
be sure to use the `Parsing.md` file in the `specifications` folder to guide
your work.

Unlike the `java` folder, there is very little scaffolding in this folder.
That is intentional.  You should decide how you want to approach this
problem.  When you have a fully working Java solution, one approach is to
simply *translate* your code into Python.  Doing so is not especially
idiomatic, and will lead to you writing a lot more code than is necessary.
The more you stray from your Java design, the more you will learn, because
you will be developing directly in a dynamically typed language.  Your IDE
will be less useful, but you'll be able to do a lot more in fewer lines of
code.  For example, the reference solution is only 420 lines of code in
parser.py, plus 94 lines in astnodes.py.  In contrast, the Java solution has
1281 lines of code (plus 371 lines of comments) in the `Parser` folder.

Please note that you should only work in the `slang_parser.py` file.

You can run the code by typing `python3 slang.py -parse filename`, where the
filename refers to the output from a correct scanner.
