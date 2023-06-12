# CSE 109 - Systems Software - Spring 2023

# Program 3 - HashSet

⏰ **Due Date: 4/10/2023 EOD**

## Instructions 

**Read thoroughly before starting your project:**

1. Fork this repository into your CSE109 project namespace. [Instructions](https://docs.gitlab.com/ee/workflow/forking_workflow.html#creating-a-fork)
2. Clone your newly forked repository onto your development machine. [Instructions](https://docs.gitlab.com/ee/gitlab-basics/start-using-git.html#clone-a-repository) 
3. As you are writing code you should commit patches along the way. *i.e.* don't just submit all your code in one big commit when you're all done. Commit your progress as you work. 

**💥IMPORTANT: You must commit frequently as you work on your project. As a general rule try to make at least one commit per function you implement.**

4. When you've committed all of your work, there's nothing left to do to submit the assignment.

## Assignment

In assignment you will implement a hash set data structure in C++. The core of the hashset is an array of linked list pointers. The type of the table is a `LinkedList**`. The first star indicates it's a pointer to an array, the second star indicates each array element holds a LinkedList pointer. The Hashset also holds its size, and the current load factor. The load factor will be recalculated on each insert. 

The Hashset struct is declared in `hashset.h`, along with a number of functions you will need to implement.

```c++
class HashSet {
  private:
    // The backbone of the hash set. This is an array of Linked List pointers.
    LinkedList** array;

    // The number of buckets in the array
    size_t size; 

    // Generate a prehash for an item with a given size
    unsigned long prehash(int item);

  public:
    // Initialize an empty hash set, where size is the number of buckets in the array
    HashSet(size_t size);

    // Free all memory allocated by the hash set
    ~HashSet();

    // Hash an unsigned long into an index that fits into a hash set
    unsigned long hash(int item);

    // Insert item in the set. Return true if the item was inserted, false if it wasn't (i.e. it was already in the set)
    // Recalculate the load factor after each successful insert (round to nearest whole number).
    bool insert(int item);

    // Remove an item from the set. Return true if it was removed, false if it wasn't (i.e. it wasn't in the set to begin with)
    bool remove(int item);

    // Return true if the item exists in the set, false otherwise
    bool contains(int item);

    // Returns the number of items in the hash set
    size_t len();

    // Returns the number of empty buckets that can be filled before reallocating (Use a threshold of 70 for resize)
    size_t capacity();

    // Print Table. You can do this in a way that helps you implement your hash set.
    void print();

};
```

💡 Tip: Feel free to use your own linked list implementation, but don't use a standard library linked list.

## Makefile

Write a Makefile inside of the project root that has the following targets:

- all - build static and shared libraries.
- static - build a static library `libhashset.a`, put it in `build/lib/release`. Put object files in `build/objects`
- shared - build a shared library `libhashset.so`, put it in `build/lib/release`. Put object files in `build/objects`
- debug - build a shared library with debug symbols, put it in `build/lib/debug`. Put object files in `build/objects`
- clean - remove all build artifacts by removing the build directory.
- install - move the shared library to `/usr/lib`
- test - compile `tests/test.cpp` and run it. Put the tests executable in `build/bin`. Put object files in `build/objects`

## Tests

Write at least 5 more tests in `tests/test.cpp`.

## Code Demo and Explanation

This is the oral portion of the homework. You will record an explanation for your data structure which demonstrates its usage and implementation. You don't have to show your face but you do have to record your voice (accommodations are available upon request). You should be sure to cover the following points in your discussion:

- **Purpose and functionality of code:** Explain what your code does and how it works.

- **Data structures:** Explain how the hash set data structure is implemented. How did you make use of the linked list?

- **Makefile:** Explain your makefile and the various targets you were asked to write. 

- **Code organization and style:** Explain how your code is organized and structured, discuss any design decisions you made. You should also discuss your coding style and any coding conventions you followed.

If you didn't finish the homework in is entirety, explain how you attempted to solve it and where you got stuck. This will get you at least some points. 

You can use Zoom to do this, [here is a link](https://support.zoom.us/hc/en-us/articles/360059781332-Getting-started-with-recording) to some instructions. You don't have to record your face, only your voice and the screen. Go through the answer and explain how you arrived there. Your goal with this question is to convince me you know what you are talking about, so I want you to do this without reading a script or written answer. Just go through line by line and explain what the program does. When you are done, upload your recording to your Lehigh Drive and add a link below. 

**💥IMPORTANT: Make sure you give blanket permission to the link holder to view the file**

Paste Recording Link(s) Here: [link](https://drive.google.com/file/d/1UUP_di6ePHDe_GXvMHFIoC55gD5bJxcM/view?usp=sharing)

## Evaluation

- Only files under version control in your forked assignment repository will be graded. Local files left untracked on your computer will not be considered.

- Only code committed *and pushed* prior to the time of grading will be accepted. Locally committed but unpushed code will not be considered.

- Your assignment will be graded according to the [Programming Assignment Grading Rubric](https://drive.google.com/open?id=1V0nBt3Rz6uFMZ9mIaFioLF-48DFX0VdkbgRUDM_eIFk).

## Tips For Success 

Some things to take into consideration when writing your assignment:

- Start this project the day it's assigned. Use all the time allocated and don't create a situation where you are starting the project right before it is due.

- At the very least, make sure your code compiles (on the Sunlab machines) before you submit it.

- Use meaningful function and variable names. This will make the code easier to read and understand, and will also make it easier to maintain in the future.

- Adding comments to the code can help the graders understand what your code is doing, which can help them assign partial points to incorrect solutions.

- Always check for errors when reading input, opening files, or allocating memory. This will help prevent crashes and other unexpected behavior.

- Free resources like file handles, socket handles, heap memory, etc. as soon as they are not needed anymore to avoid resource leaks.

- Using the right data type can make your program more efficient and less error-prone. For example, use integers when working with whole numbers, and use floating-point numbers when working with decimal numbers. With systems software especially, we want to pay attention to this component of sofware design.

- Test your code: Before releasing your code, make sure to test it thoroughly. Try to anticipate how users will use your program and test it under different conditions to make sure it works as expected.


