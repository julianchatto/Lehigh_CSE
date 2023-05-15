#include <stdio.h>
#include "functions.h"

/*
    In the process of generating a executable from object files and libraries, 
    we can choose when the linking of these files occurs   
        - compile time (static)
        - runtime (dynamic)


    // static linking
        - use are to compile one or more object files into a lib
        - filename has the form "libmy_li.a"
        - name of library: my_lib
        - "lib" prefix
        - ".a" extension
        - library is emedded inside the executable at link time
        pros:
            - makes distribution easy
            - makes compilation easy
            - faster startuptime
        cons: 
            - makes executable larger
            - makes compile time longer

    // dynamic linking
        - use gcc to compile object file
        - use gcc to archive into dynamic lib
        - filename has the form "libmy_li.so"
            - name of library: my_lib
            - "lib" prefix
            - ".so" extension (shared object)
        - found and linked dynamically at runtime

    pros: 
        - smaller file sizes
        - keeps overall system memory usage down (but not really)
        - keep libs up to date without recompiling binary (but not in practice)
    cons: 
        - harder to distribute binary executabels to end users
        - slower startup time

*/


/*
    use gcc main.c -c to allow it to compile even without the functions
    then gcc functions.c -c
        - at this point you have functions.c,.h,.o and main.c,.o
    then ar rs libfunctions.a functions.o
        - know have libfunctions.a
    to static link
        - gcc main.o -L. -lfunctions -o main
*/

int main(void) {
  int x = function_one(1);
  int y = function_two(5);
  return x+y;
}