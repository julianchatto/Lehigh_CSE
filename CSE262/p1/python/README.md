# CSE 262 Assignment 1: Python

In this part of the assignment, you will write our five programs in
Python. The Python3 interpreter is available on the SunLab as `python3`, or
you can install it on your local machine.  Please make sure that your code is
compatible with the version of Python on the sunlab (v 3.9.13), which is
fairly new, but lacks some nice features added in Python 3.10.

If you are used to Java, Python may feel loose and unstructured, because you
rarely specify the types of variables.  In addition to a different syntax, as
a purely interpreted language, Python is able to offer some powerful features
that would be extremely complex to implement in other languages.

## Building and Running the Code

As a purely interpreted language, you can run python programs with
invocations like `python3 my_map.py`.  Be sure to read
<https://stackoverflow.com/questions/419163/what-does-if-name-main-do> and
think about how to create a guard so that your tests will run if your file is
invoked directly from `python3`, but not if it is imported.

## Turn-In Details

You should do all of your work for this part of the assignment in the five
`.py` files in this folder.  When you have something worth turning in, you
can use a variation on this sequence of commands:

```bash
git add read_list.py
git commit -m "<your commit message here>"
git push
```

## Documentation Details

In each file, you should comment your code extensively, to describe what it
is doing.  This will be graded.  You should write your comments assuming that
the audience is someone who has never programmed in Python before, but who
knows Java well.  Be verbose!
