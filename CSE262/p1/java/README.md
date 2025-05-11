# CSE 262 Assignment 1: Java

In this part of the assignment, you will write our five programs in Java. The
Java Development Kit (JDK) is available on the SunLab as `javac` and `java`,
or you can install it on your local machine.  The default version of Java on
the SunLab is 11.0.25.

## Building and Running the Code

To simplify the build process, this code uses `gradle` to manage building.
To build the program in a unix environment, you should be able to type:

```bash
./gradlew build
```

If there are no compilation errors, you should see something like this:

```bash
BUILD SUCCESSFUL in 744ms
5 actionable tasks: 5 executed
```

You can run the program like this:

```bash
java -jar app/build/libs/app.jar
```

You should feel free to edit the `main` function in `App.java` so that it does
extensive testing of your solutions.

You can clean up intermediate files by typing:

```bash
./gradlew clean
```

## Turn-In Details

You should do all of your work for this part of the assignment in the five
`.java` files in the `app/src/main/java/edu/lehigh/cse262/p1` subfolder.
When you have something worth turning in, you can use a variation on this
sequence of commands:

```bash
git add app/src/main/java/edu/lehigh/cse262/p1/read_list.java
git commit -m "<your commit message here>"
git push
```

## Documentation Details

In each file, you should comment your code extensively, to describe what it
is doing.  This will be graded.  You should write your comments assuming that
the audience is someone who has a CSE 17 level of familiarity with Java.  Be
verbose!
