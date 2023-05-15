#include <stdio.h>
#include <stdlib.h>


class Stack {
    private:
        int* array;
        int capacity;
        int size;
    public: 
        Stack();
        Stack(int capacity);
        ~Stack();
        bool push(Stack* stack, int value);
} Stack;

// :: - new operator in C++
//      scoping operator, scopes a identifier to a new namespace

// class constructor - special method that creates objects in the class called with "new" keyword
// class destructor - special method that destroyes/fres objects in the class called with delete keyword 
void Stack::Stack(int capacity) {
    printf("CAPACITY CONSTRUCTOR\n");
    int* array = (int*)malloc(capacity * 4);
    this->capacity = capacity;
    this->size = 0;
    this->array = array;
} 
void Stack::Stack() {
    printf("DEFAULT CONSTRUCTOR\n");
    int* array = (int*)malloc(10 * 4);
    this->capacity = 10;
    this->size = 0;
    this->array = array;
}

bool Stack::push(int value) {
    printf("STACK PUSH\n");
    this->array[this->size] = value;
    if (this->size < this->capacity) {
        this->size++;
        return true;
    } else {
        return false;
    }
}

Stack::~Stack() {
    printf("DESTROYING STACK\n");
    free(this->array);
}


int main() {
     Stack* s = new Stack(20);
  s->push(10);
  delete s;
    
}