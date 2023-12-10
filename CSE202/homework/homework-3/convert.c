#include <stdio.h>
char decode(int x, unsigned n) { 
    return ((x & n) & x) << n;
}

unsigned float_twice(unsigned f) {
    unsigned f_copy = f;
    unsigned sign = 0;
    sign |= f_copy >> 31;
    f_copy <<= 1;
    unsigned exponent = 0;
    exponent |= f_copy >> 24;
    f_copy <<= 8;
    f_copy >>= 9;
    unsigned fraction = f_copy;

    if (exponent == 255 || exponent == 2047) { // NaN or Infinity
        return f;
    }

    if (exponent == 0) { // Denormalized
        fraction <<= 1;
        if (fraction < f_copy) { // Overflow
            exponent++;
        }
    } else { // Normalized
        exponent++;
        if (exponent == 255) { // Infinity TODO: How to return infinity
            return 0x7F800000;
        }
    }
    return (sign << 31) | (exponent << 23) | fraction;
}

int main() {
    printf("0x%08x\n", float_twice(0x3F800000)); // 1.0 -> 2.0
    printf("0x%08x\n", float_twice(0x40000000)); // 2.0 -> 4.0
    printf("0x%08x\n", float_twice(0x7F800000)); // Infinity -> Infinity
    printf("0x%08x\n", float_twice(0x7FC00000)); // NaN -> NaN
    printf("0x%08x\n", float_twice(0x00000001)); // Smallest denormalized -> larger denormalized
    printf("0x%08x\n", float_twice(0x007FFFFF)); // Largest denormalized -> smallest normalized
    return 0;
}

/* compute 2*f. If f is NaN or f is Infinity, then return f */

