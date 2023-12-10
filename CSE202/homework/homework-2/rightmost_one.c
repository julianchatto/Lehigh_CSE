#include <stdio.h>
#include <stdlib.h>

/*
* Generate mask indicating rightmost 1 in x. Assume w = 32.
* For example, 0xFF00 -> 0x0100, and 0x66C0 -> 0x0040
* If x=0, then return 0.
*/
int rightmost_one(unsigned x) {
    return x & -x;
}

int tsub_ok(int x, int y) {
    return !((x > 0 && y < 0 && x - y <= 0) || (x < 0 && y > 0 && x - y >= 0));
}

int mul5div8(int x) {
    return (((x << 2) + (x << 0)) + ((x >> 31) & (((1 << 3) - 1)))) >> 3;
}

int main() {
    printf("%d\n", mul5div8(-7));
}