#include <stdlib.h>
#include "my_lib.h"
#include <stdio.h>


// 1. Compile main.c into main.o (object file)
    //a) gcc main.c -c
// 2. Compile my_lib.c into my_lib.o 
    //a) gcc my_lib.c -c
// 3. Compress my_lib.o and libmylib.a 
    // a) ar rf libmy_lib.a my_lib.o
// 4. Link main.c with libmylib.a into executable
    // a) gcc main.o -lmy_lib -L.
    // -c (skip linker)
    // -l (specify search path for lib)
    // -L (specify search path for target library)

int main(int argc, char **argv) {
  printf("%d", my_function(2));
    return 0;
}