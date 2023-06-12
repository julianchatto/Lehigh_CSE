# CSE109 - Systems Software - Spring 2023

# Program 4 - implementing a Protocol: Pack109

**Due Date: 5/1/2023 EOD**

## Instructions 

**Read thoroughly before starting your project:**

1. Fork this repository into your CSE109 project namespace. [Instructions](https://docs.gitlab.com/ee/workflow/forking_workflow.html#creating-a-fork)
2. Clone your newly forked repository onto your development machine. [Instructions](https://docs.gitlab.com/ee/gitlab-basics/start-using-git.html#clone-a-repository) 
3. As you are writing code you should commit patches along the way. *i.e.* don't just submit all your code in one big commit when you're all done. Commit your progress as you work. **You should make at least one commit per function.**
4. When you've committed all of your work, there's nothing left to do to submit the assignment.

## Assignment

Networking involves the communication between two or more computers over a channel, which can be either wired or wireless. This communication occurs through the exchange of byte streams, where bytes from one computer are transmitted to another, prompting the receiving computer to perform a specific action. To correctly interpret these bit streams, a protocol must be established to regulate the exchange of information; otherwise, the byte stream could be interpreted in various ways. The initial step in network communication is converting variables and data structures within your program into a binary format suitable for transmission over the channel.

In this assignment, you will develop several functions that convert data types and data structures in C++ into a vector of bytes (serialization, or "ser"). Additionally, you will write a set of functions that reverse this process, converting vectors of bytes back into their original data types or data structures (deserialization, or "de").

### Deliverables

The deliverables for this assignment are:

- serde function implementations
- unit tests for all implemented functions

#### Functions

There are a number of serialization and deserialization functions stubbed out in `include/pack109.hpp` that you are to implement in `src/lib.cpp`. I've implemented 4 of them for you as examples. You are to implement the rest. In addition, you are to write a test for each of them in `test/test.cpp`. I've given you examples of how to write these tests for the 4 functions I've implemented. One of the tests fails intentionally (fix it). I've also provided a function to print the byte vector contents. It may seem like a lot of functions at first, but many of them are similar with the only difference being an adjustment to the number of times a loop iterates

To run the tests, do `make test` and then run the test program in `build/bin/release/test`.

**REMEMBER** you must make at least one commit per function.

#### Tests

When you are done, you should have at least the following tests:

- serde tests for true and false (4)
- serde tests for u8, u32, u64, i8, i32, i64 (12)
- serde tests for f32 and f64 (4)
- serde tests for s8 (2)
- serde tests for a8 for vectors of u8 (2)
- serde tests for m8 for the Person struct (2)

For a total of 26 tests.

### Pack109 Object Serialization Format

Pack109 is a binary object serialization format that flattens a number of different datatypes into a format suitable for transmission over a channel.

#### Overview

In the table below I've listed the 16 available objects in the Pack109 serialization format.

| Type                    | Tag (Hex) | 
| ----------------------- | --------- |
| Bool (true)             |   0xa0    |
| Bool (false)            |   0xa1    |
| Unsigned 8-bit Integer  |   0xa2    |
| Unsigned 32-bit Integer |   0xa3    |
| Unsigned 64-bit Integer |   0xa4    |
| Signed 8-bit Integer    |   0xa5    |
| Signed 32-bit Integer   |   0xa6    |
| Signed 64-bit Integer   |   0xa7    |
| 32-bit Floating Number  |   0xa8    |
| 64-bit Floating Number  |   0xa9    |
| 8-bit String            |   0xaa    |
| 16-bit String           |   0xab    |
| 8-bit Array             |   0xac    |
| 16-bit Array            |   0xad    |
| 8-bit Map               |   0xae    |
| 16-bit Map              |   0xaf    |

#### Notation Key

```
one byte:
┌────────┐
│        │
└────────┘

a variable number of bytes:
╔════════╗
║        ║
╚════════╝

variable number of objects stored in Pack109 format:
╭╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╮
┆                 ┆
╰╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╯
```

#### Bool

Bool is for serializing `true` and `false` boolean values. Although we can represent these values in one bit in C and C++, we have to use 8 bits to represent them in our serialized format.

```
true:
┌────────┐
│  0xa0  │
└────────┘ 

false:
┌────────┐
│  0xa1  │
└────────┘ 
```

#### Integers

Integers are for storing signed and unsigned `chars`, `ints`, and `longs`.

```
u8 stores a 8-bit unsigned integer
┌────────┬────────┐
│  0xa2  │ZZZZZZZZ│
└────────┴────────┘ 

u32 stores a 32-bit big-endian unsigned integer
┌────────┬────────┬────────┬────────┬────────┐
│  0xa3  │ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│
└────────┴────────┴────────┴────────┴────────┘ 

u64 stores a 64-bit big-endian unsigned integer
┌────────┬────────┬────────┬────────┬────────┬────────┬────────┬────────┬────────┐
│  0xa4  │ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│
└────────┴────────┴────────┴────────┴────────┴────────┴────────┴────────┴────────┘ 

i8 stores a 8-bit signed integer
┌────────┬────────┐
│  0xa5  │ZZZZZZZZ│
└────────┴────────┘ 

i32 stores a 32-bit big-endian signed integer
┌────────┬────────┬────────┬────────┬────────┐
│  0xa6  │ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│
└────────┴────────┴────────┴────────┴────────┘


i64 stores a 64-bit big-endian signed integer
┌────────┬────────┬────────┬────────┬────────┬────────┬────────┬────────┬────────┐
│  0xa7  │ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│
└────────┴────────┴────────┴────────┴────────┴────────┴────────┴────────┴────────┘

```

#### Floats

Floats are for serializing big-endian IEEE 754 single and double precision floating point numbers like `floats` and `doubles`. Extension of precision from single-precision to double-precision does not lose precision.

```
f32 stores a single-width floating point number
┌────────┬────────┬────────┬────────┬────────┐
│  0xa8  │ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│
└────────┴────────┴────────┴────────┴────────┘

f64 stores a double-width floating point number
┌────────┬────────┬────────┬────────┬────────┬────────┬────────┬────────┬────────┐
│  0xa9  │ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│ZZZZZZZZ│
└────────┴────────┴────────┴────────┴────────┴────────┴────────┴────────┴────────┘
```

#### Strings

Strings are for serializing strings and c-strings. If you want to serialize an array of bytes that aren't meant to represent characters, use an Array instead (see below).

```
s8 stores a byte array whose length is up to (2^8)-1 bytes:
┌────────┬────────┬════════╗
│  0xaa  │YYYYYYYY│  data  ║
└────────┴────────┴════════╝

YYYYYYYY is a 8-bit unsigned integer which represents the length of the data

s16 stores a byte array whose length is up to (2^16)-1 bytes:
┌────────┬────────┬────────┬════════╗
│  0xab  │ZZZZZZZZ│ZZZZZZZZ│  data  ║
└────────┴────────┴────────┴════════╝

ZZZZZZZZZZZZZZZZ is a 16-bit unsigned integer which represents the length of the data
```

#### Arrays

Arrays are for serializing arrays of homogenous datatypes. Each object in the serialized array should be one of the other objects in the Pack109 spec. For example, you can serialize an array of u8s, an array of strings, or even an array of arrays.

```
a8 stores an array whose length is up to (2^8)-1 elements:
┌────────┬────────┬╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╮
│  0xac  │YYYYYYYY│    N objects    ┆
└────────┴────────┴╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╯

YYYYYYYY is a 8-bit unsigned integer which represents the length of the data

a16 stores an array whose length is up to (2^16)-1 elements:
┌────────┬────────┬────────┬╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╮
│  0xad  │ZZZZZZZZ│ZZZZZZZZ│    N objects    ┆
└────────┴────────┴────────┴╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╯

ZZZZZZZZZZZZZZZZ is a 16-bit unsigned integer which represents the length of the data
```

#### Maps

Maps are for serializing datastructures that can be represented by key-value pairs. This includes associative arrays, structs, and objects.

```
m8 stores a map whose length is upto (2^8)-1 elements
┌────────┬────────┬╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╮
│  0xae  │YYYYYYYY│   N*2 objects   ┆
└────────┴────────┴╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╯

YYYYYYYY is a 8-bit unsigned integer which represents the length of the data

m16 stores a map whose length is upto (2^16)-1 elements
┌────────┬────────┬────────┬╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╮
│  0xaf  │ZZZZZZZZ│ZZZZZZZZ│   N*2 objects   ┆
└────────┴────────┴────────┴╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╯

ZZZZZZZZZZZZZZZZ is a 16-bit unsigned integer which represents the length of the data
```

For example, consider the following struct:

```
struct Person {
  char age;
  float height;
  string name;
}
```

You can serialize this into a map of maps. Let's say we have the following values in our struct:

```
struct Person ann = { age: 10, height: 3.4, name: "Ann" };
```

We could serialize this into the following byte vector:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│0xae   // map tag                                                            │
│0x01   // 1 kv pair                                                          │
├─────────────────────────────────────────────────────┬───────────┬───────────┤
│0xaa   // string8 tag                                │           │           │
│0x06   // 6 characters                               │ key       │           │
│Person // the string "Person"                        │           │           │
├─────────────────────────────────────────────────────┼───────────┤  pair 1   │
│0xae   // the value associated with the key is a map │           │           │
│0x03   // 3 kv pairs                                 │           │           │
├──────────────────────────────┬───────────┬──────────┤           │           │
│0xaa   // string8 tag         │           │          │           │           │
│0x03   // 3 characters        │ key       │          │           │           │
│age    // the string "age"    │           │          │           │           │
├──────────────────────────────┼───────────┤ pair 1   │           │           │
│0xa2   // u8 tag              │ value     │          │           │           │
│0x0a   // 10                  │           │          │           │           │
├──────────────────────────────┼───────────┼──────────┤           │           │
│0xaa   // string8 tag         │           │          │ value     │           │
│0x06   // 6 characters        │ key       │          │           │           │
│height // the string "height" │           │          │           │           │
├──────────────────────────────┼───────────┤ pair 2   │           │           │
│0xa8   // f32 tag             │ value     │          │           │           │
│3.4    // float value 3.4     │           │          │           │           │
├──────────────────────────────┼───────────┼──────────┤           │           │
│0xaa   // string8 tag         │           │          │           │           │
│0x04   // 4 characters        │ key       │          │           │           │
│name   // the string "name"   │           │          │           │           │
├──────────────────────────────┼───────────┤ pair 3   │           │           │
│0xaa   // string8 tag         │           │          │           │           │
│0x03   // 3 characters        │ value     │          │           │           │
│Ann    // the string "Ann"    │           │          │           │           │
└──────────────────────────────┴───────────┴──────────┴───────────┴───────────┘           
```

The total length of this byte vector would be 43 bytes.
