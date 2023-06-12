#include <stdlib.h>   
#include <stdio.h>    
#include <string.h>   
#include <errno.h>
#define PAGE 4096                                                        /* no changes */
#define KILO 1024                                                        /* no changes */


void *Malloc(int numBytes);

int main() {
  unsigned int i;                                                        /* no changes */
  char **memptr = Malloc(KILO * KILO * sizeof(char *));                  /* no changes */
  for (i = 0; i < KILO * KILO; i++) {                                    /* do not add add'l statements to loop body until step 9 */
    memptr[i] = Malloc(PAGE);                                            /* no changes */
    memptr[i][KILO] = 255;                                               /* no changes */
    if (i < 20 && i%2==0) {
      printf("Block: %d, %p\n", i/2, &memptr[i]);
    }
  }
 
  for (i =0; i < KILO*KILO; i++) {
    free(memptr[i]);
  }
  free(memptr);
}

void *Malloc(int numBytes) {                                             /* no changes */
  void *ptr = NULL;                                                      /* no changes */
  if ((ptr = malloc(numBytes)) == NULL) {                                /* no changes */
    fprintf(stderr, "Could not allocate space - %s", strerror(errno));   /* no changes */
    exit(99);                                                            /* no changes */
  }
  return ptr;
}

/*
 * Q0: What does this program do?
 * A0: This program allocates memory dynamically for an array of pointers (2d array). The size of the array is KILO*KILO by Kilo. 
 *
 * Q1: How many bytes are between the pointers to the allocated memory? (answer in decimal)
 * A1: The number of bytes between the pointers and the allocated memory is 10.
 *
 * Q2: Is this the amount you expected? Why or why not?
 * A2: This is not the amount I expectd because it contains the pointer 8 bytes, the char which is 1 byte which is only 9 bytes. 
       This probably means that malloc added 1 byte of padding between the each block to make memory better. 
 */


/* List of compiler errors
added int return type  in main
removed main method parameters because they were unused
added include statments - for different code functions
return ptr - no return type for Malloc
void *Malloc(int numBytes); - a function signature for Malloc 
*/

/*
Memory leak errors

The memory allocated for each the array was never freed. To fix this I used a for loop to free each index 
of memptr and then at the end freed the actual variable. 
*/