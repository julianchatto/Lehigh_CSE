#include <stdio.h>

int main(int argc, char** argv) {

    // To declare a pointer, put a star on a type
    // int* - an int pointer. Points to a memory address of 4 bytes 
    // char* - an char pointer. Points to a memory address of a 1 byte
    // float* - a float pointer.  Points to a memory address of 4 bytes 


    // * - dereference operator. Returns that to which pointer points
    // & - reference operator. Returns memory address of pointer
    int* int_ptr; // declares a pointer, returns memory address
    int x = 0xABCDEF12;
    int_ptr = &x;

    printf("%p", int_ptr);
    printf("%p", *int_ptr); // pooting a * in front of a pointer, dereference: goes to memory address and returns the actual thing it references
  
  return 0;
}