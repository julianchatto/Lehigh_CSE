/* 
 * Program: lab4.c
 * Name: Julian Chattopadhyay
 * UID: juc226
 */

/* preprocessor directives */
#include <stdio.h>
#include <string.h>

/* prototypes - DO NOT CHANGE EITHER OF THESE */
void printViaArrForm(char *[], const char *);  /* pass argv or envp, pass "argv" or "envp" */
void printViaPtrForm(char **, const char *);   /* pass argv or envp, pass "argv" or "envp" */

/* main - DO NOT CHANGE ANYTHING IN HERE */
int main(int argc, char **argv, char *envp[]) {  /* could use "char *argv[]" and/or "char **envp" */
  putc('\n', stdout);
  fprintf(stdout, "argc (at address %p) has value %d\n", &argc, argc);  /* print location and value of argc */
  fprintf(stdout, "argv (at address %p) has value %p\n", &argv, argv);  /* print location and value of argv */
  fprintf(stdout, "envp (at address %p) has value %p\n", &envp, envp);  /* print location and value of envp */
  putc('\n', stdout);
  printViaArrForm(argv, "argv");  /* print the command-line arguments */
  putc('\n', stdout);
  printViaArrForm(envp, "envp");  /* print the environment variables  */
  putc('\n', stdout);
  printViaPtrForm(argv, "argv");  /* print the command-line arguments */
  putc('\n', stdout);
  printViaPtrForm(envp, "envp");  /* print the environment variables  */
  putc('\n', stdout);
  return 0;
}

/* functions */
void printViaArrForm(char *dp[], const char *sp) {  /* all references to elements of dp must use square brackets "[ ]" */
  fprintf(stdout, "%s (at address %p) points to address %p\n", sp, &dp, &dp[0]); 
  int i;
  for (i = 0; dp[i] != NULL; i++) {
    fprintf(stdout, "\t%s[%d] (at address %p)  points to address %p where this string of %ld bytes is stored: '%s'\n", sp, i, &dp[i], dp[i], strlen(&*dp[i]), &*dp[i]);
  }
  fprintf(stdout, "\t%s[%d] (at address %p)  is (null)\n", sp, i, &dp[i+1]);

}

void printViaPtrForm(char **dp, const char *sp) {  /* all references to elements of dp must use pointer arithmetic */
  fprintf(stdout, "%s (at address %p) points to address %p\n", sp, &dp, &*(dp + 0));
  int i;
  for (i = 0; *(dp+i) != NULL; i++) {
    fprintf(stdout, "\t%s[%d] (at address %p)  points to address %p where this string of %ld bytes is stored: '%s'\n", sp, i, (dp+i), *(dp+i), strlen(*(dp+i)), &**(dp+i));
  }
  fprintf(stdout, "\t%s[%d] (at address %p)  is (null)\n", sp, i, &*(dp + i));
}

/* Q & A 
 *
 * Q1. What is envp (in terms of its data structure)?
 * A1. It is a pointer to an array of chars.
 * 
 * Q2. Why is the address where envp is stored 8 bytes away from where argv is stored?
 * A2. envp memory is allocated directly after argv. And since, envp is a pointer TO an array and a pointer is 8 bytes, envp is 8 bytes after argv 
 * 
 * Q3. Why are the addresses of argv and envp different when printed from the function printViaArrForm then when printed from main?
 * A3. They are different because argc and envp are passed by value. Therefore, a copy of them is made which means that their addresses are different. 
 *
 * Q4. Why are the values of argv and envp the same whether they were printed from printViaArrForm or main?
 * A4. They are the same because argc and envp are passed by reference. Therefore, the memory address are the same in both main and the function. 
 *
 * Q5. Why are arrays considered pointers, or conversely, why can pointers be viewed as arrays?
 * A5. Pointers can be viewed as arrays because pointers point to the next element. While the arrays head points to the first element and then each successive element is pointed to by the previous element.
 *
 */
