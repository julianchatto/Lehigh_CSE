#include <stdlib.h>
int main(void) {

    // use afre free bug
    // undefined behavior
    int* x = (int*)malloc(4);
    *x = 10;
    free(x);

    printf("%d", x);

    return 0;
}