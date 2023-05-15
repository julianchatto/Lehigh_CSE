int* x;
int y = 0xABCDEF12;
x = &y;

*x  // 0xABCDEF12
*(char*)x // {0xAB, 0xCD, 0xEF, 0xGH} -> an array of 4 bytes == an integer 

void* foo = (void*)malloc(8); // gives 8 bytes

char* v1 = (char*)foo; // points to array of 8 chars
int* v1 = (int*)foo; // points to an array of 2 ints
double* v2 = (double*)foo; // points to a double 

List* list_pointer = (List*)malloc(sizeof(List*));
initList(list_pointer); // returns 
insert_at_head(myList, &10) //2nd param needs to be a param