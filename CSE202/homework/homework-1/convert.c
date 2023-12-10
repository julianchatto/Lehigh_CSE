#include <stdio.h>
#include <string.h> 

int powof16(int num) {
    int result = 1;
    for (int i = 0; i < num; i++) {
        result *= 16;
    }
    return result;
}
void hexToBin(char hex) {
    switch (hex) {
        case '0': 
            printf("0000"); 
            break;
        case '1': 
            printf("0001");
            break;
        case '2': 
            printf("0010"); 
            break;
        case '3': 
            printf("0011"); 
            break;
        case '4': 
            printf("0100"); 
            break;
        case '5': 
            printf("0101"); 
            break;
        case '6': 
            printf("0110"); 
            break;
        case '7': 
            printf("0111"); 
            break;
        case '8':
            printf("1000"); 
            break;
        case '9':
            printf("1001"); 
            break;
        case 'A': 
        case 'a': 
            printf("1010"); 
            break;
        case 'B': 
        case 'b': 
            printf("1011"); 
            break;
        case 'C': 
        case 'c': 
            printf("1100"); 
            break;
        case 'D': 
        case 'd': 
            printf("1101"); 
            break;
        case 'E': 
        case 'e':
            printf("1110"); 
            break;
        case 'F':
        case 'f': 
            printf("1111"); 
            break;
    }
}
int getDec(char hex) {
    switch (hex) {
        case '0': 
            return 0;
            break;
        case '1': 
            return 1;
            break;
        case '2': 
            return 2;
            break;
        case '3': 
            return 3;
            break;
        case '4': 
            return 4;
            break;
        case '5': 
            return 5; 
            break;
        case '6': 
            return 6;
            break;
        case '7': 
            return 7; 
            break;
        case '8':
            return 8; 
            break;
        case '9':
            return 9;
            break;
        case 'A': 
        case 'a': 
            return 10;
            break;
        case 'B': 
        case 'b': 
            return 11;
            break;
        case 'C': 
        case 'c': 
            return 12; 
            break;
        case 'D': 
        case 'd': 
            return 13; 
            break;
        case 'E': 
        case 'e':
            return 14; 
            break;
        case 'F':
        case 'f': 
            return 15; 
            break;
    }
}
void convert(char* hex) {
    // convert to binary
    printf("bin: ");
    int length = strlen(hex);
    for (int i = 0; i < length; i++) {
        hexToBin(hex[i]);
    }

    // convert to decimal
    long sum = 0;
    int j = length - 1;
    for (int i = 0; i < length; i++) {
        sum += getDec(hex[i]) * powof16(j);
        j--;
    }
    printf(" dec: %d\n", sum);

}
int main() {
    convert("AA");
}

