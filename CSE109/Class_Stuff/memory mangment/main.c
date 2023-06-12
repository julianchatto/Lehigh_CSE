#include <stdio.h>
#include <stdlib.h>



int* allocate_array(int n) {
    int* x = (int*)malloc(n*4);
    for (int i = 0; i < n i++) {
        x[i] = 0;
    }
    return x;
}
int main(int argc, char** argv) {

  int n = atoi(argv[1]);
  
  printf("How many? %d\n", n);

  int* numbers = allocate_array(n); // allocate 40 bytes
  
  numbers[2] = 123;
  
  printf("%p\n", numbers);
  printf("%p\n", &numbers[0], numbers + 0)   
  printf("%p\n", &numbers[1])   
  printf("%p\n", &numbers[2])   
  printf("%p\n", numbers[2]);
  free(numbers); // free 40 bytes
  return 0;
}