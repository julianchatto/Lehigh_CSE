#include <stdio.h>
#include <iostream>

void print_binary() {
    int num = 0xABC123;
    int numBits = 32;
    for (int i = numBits-1; i>= 0; --i ) {
        int bit = (num>>i) & 1;
        std::cout << bit;
    }
    std::cout << std::endl;
}

int main(void) {
    print_binary();
}