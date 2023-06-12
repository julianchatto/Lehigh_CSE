# CSE109 - Systems Software - Spring 2023

# Program 2 - Implementing and Evaluating a Data Structure in C

**⏰ Due Date: 4/2/2023 EOD**

## Instructions 

**Read thoroughly before starting your project:**

1. Fork this repository into your CSE109 project namespace. [Instructions](https://docs.gitlab.com/ee/workflow/forking_workflow.html#creating-a-fork)
2. Clone your newly forked repository onto your development machine. [Instructions](https://docs.gitlab.com/ee/gitlab-basics/start-using-git.html#clone-a-repository) 
3. As you are writing code you should commit patches along the way. *i.e.* don't just submit all your code in one big commit when you're all done. Commit your progress as you work. **You should make at least one commit per function.**
4. When you've committed all of your work, there's nothing left to do to submit the assignment.

## Assignment

You will implement a linked list data structure in this assignment. The linked list will consist of a `head` pointer and a tail pointer. The head pointer will point to a `Node` struct, which will hold a data void pointer as well as a pointer to the next node in the list. The `tail` pointer will point to the final node in the linked list. The `next` pointer on the tail will point to `NULL`.


```
  ┌─────────┐    ┌─────────┐   ┌─────────┐    ┌─────────┐
  │     head│--->│  void*  │-->│  void*  │--->│  void*  │---> NULL
  │         │    └─────────┘   └─────────┘    └─────────┘
  │     tail│--------------------------------------^
  └─────────┘
```


The `List` and `Node` structs are declared in `linkedlist.h`, along with a number of functions you will need to implement.

```c
// Initialize an empty list. The head and tail pointers should point to NULL.
void initList(List* list_pointer);

// Create Node struct containing a void pointer to an item, return pointer to the newly created Node struct
Node* createNode(void* item);

// Insert new Node at the end of the list, containing a void pointer to item. The next pointer on this Node
// points to NULL. On success return 0. On failure, return 1.
int insertAtTail(List* list_pointer, void* item);

// Insert a Node at the head of the list, containing a void pointer to item. The next pointer on the
// newly created node points to the previous head of the list. On success return 0. On failure, return 1.
int insertAtHead(List* list_pointer, void* item);

// Insert a Node at the specified index, containing a void pointer to item. If the index is greater than
// the length of the list, the program should crash. On success return 0. On failure, return 1.
int insertAtIndex(List* list_pointer, int index, void* item);

// Remove Node from the end of list and return the void pointer it contains. The preceeding Node should
// point to NULL after this operation. On failure return a NULL pointer.
void* removeTail(List* list_pointer);

// Remove Node from start of list and return return the void pointer it contains. The following Node should 
// be the new head of the list. On failure return a NULL pointer.
void* removeHead(List* list_pointer);

// Insert Node item at a specified index and return return the void pointer it contains. The Node at the specified
// index before this function is called should be the next Node following the newly inserted Node.
// On failure return a NULL pointer.
void* removeAtIndex(List* list_pointer, int index);

// Return the pointer stored in the Node at the specified index. On failure return a NULL pointer. 
void* itemAtIndex(List* list_pointer, int index);

// Return true if the list contains the given item at least once, false otherwise.
bool contains(List* list_pointer, void* item);

// Returns the size of the list, measured in nodes.
int size(List* list_pointer);
```

There is a print function that is already implemented for you. This will help you inspect the list as you write the above functions.

🤔 Hint: Feel free to implement any helper functions you need beyond the required functions.

## Evaluation

For this part of the assignment, you will evaluate your linked list implementation to verify that it's working correctly. To do this, you will write a program that reads in a test file containing byte code strings that test the functionality of your linked list data structure. The test code is found in the file named "tests", found in the root directory of this assignment respository.

Byte code is a low-level representation of code that can be executed by a virtual machine or interpreter. In this case, the byte code is a series of commands that specify how to modify, query, and assert the state of the linked list data structure. You can find a grammar for the byte code in the test file itself.

Your program will serve as an interpreter for this byte code, executing each command in turn and verifying that the output matches the expected result. The interpreter will provide a way to test the linked list in a controlled and repeatable way, allowing you to catch bugs and edge cases that may not be apparent during manual testing.

Your program should:

- Read in the test file from disk.
- Parse the test file to extract the byte code for each test.
- Create a linked list data structure.
- Iterate through each byte code string in the test file.
- For each byte code string, execute the corresponding linked list operation (insert node, remove node, get value, etc.).
- Verify that the output of the linked list operation matches the expected output.
- Print the test results to the console. Indicate whether the tests all pass, and if not, which ones failed.

📝 Note: The tests file includes 10 example tests, but your program will be tested against more that are not included in the assignment.

### Additional Tests

Write 5 additional tests, and include them in the `tests` file with the others. As with the included tests, your tests should have a desscription, an explanation of what it does, and the test itself on its own line.

## Code Demo and Explanation

This is the oral portion of the homework. You will record an explanation for your data scructure which demonstrates its usage and implementation. You don't have to show your face but you do have to record your voice (accomodations are available upon request). You should be sure to cover the following points in your discussion:

- **Purpose and functionality of the program:** Explain what the program does and how it works.

- **Input and output:** Discuss the format of the test file and the expected output for each test. Explain how the program reads in the test file and generates the output.

- **Data structures:** Explain how the linked list data structure is implemented and how it is modified by the byte code commands.

- **Parsing:** Discuss how the program parses the byte code commands and extracts the necessary parameters.

- **Interpreting:** Explain how the program interprets the byte code commands and executes the corresponding linked list operations.

- **Testing:** Discuss the importance of testing and how the byte code format allows for easy and repeatable testing of the linked list data structure.

- **Code organization and style:** Explain how their code is organized and structured, and should discuss any design decisions they made. They should also discuss their coding style and any coding conventions they followed.

If you didn't finish the homework in is entirety, explain how you attempted to solve it and where you got stuck. This will get you at least some points. 

You can use Zoom to do this, [here is a link - 2 videos because I forgot to show that the code ran](https://drive.google.com/drive/folders/1rUoez3LhHDm_daohS0Tc4doRezUw2T0B?usp=share_link) to some instructions. You don't have to record your face, only your voice and the screen. Go through the answer and explain how you arrived there. Your goal with this question is to convince me you know what you are talking about, so I want you to do this without reading a script or written answer. Just go through line by line and explain what the program does. When you are done, upload your recording to your Lehigh Drive and add a link below. 

**💥IMPORTANT: Make sure you give blanket permission to the link holder to view the file**

Recording Link(s): https://drive.google.com/drive/folders/1rUoez3LhHDm_daohS0Tc4doRezUw2T0B?usp=share_link

## Tips For Success 

Some things to take into consideration when writing your assignment:

- Start this project the day it's assigned. Use all the time allocated and don't create a situation where you are starting the project right before it is due.

- At the very least, make sure your code compiles before you submit it.

- Use meaningful function and variable names. This will make the code easier to read and understand, and will also make it easier to maintain in the future.

- Adding comments to the code can help the graders understand what your code is doing, which can help them assign partial points to incorrect solutions.

- Always check for errors when reading input, opening files, or allocating memory. This will help prevent crashes and other unexpected behavior.

- Free resources like file handles, socket handles, heap memory, etc. as soon as they are not needed anymore to avoid resource leaks.

- Using the right data type can make your program more efficient and less error-prone. For example, use integers when working with whole numbers, and use floating-point numbers when working with decimal numbers. With systems software especially, we want to pay attention to this component of sofware design.

- Test your code: Before releasing your code, make sure to test it thoroughly. Try to anticipate how users will use your program and test it under different conditions to make sure it works as expected.


