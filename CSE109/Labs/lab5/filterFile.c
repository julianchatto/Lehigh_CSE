#include <stdio.h>    /* include for fprintf and getline*/
#include <stdlib.h>   /* include for exit and free */
#include <string.h>   /* include for strlen */
#include "syscalls.h" /* include for wrapped system calls */

int main(int argc, char *argv[]) {
  
  /* Add a check to make sure at least two arguments were passed to the program.
   *   If not, print "Usage" message to stderr and exit with return code of 99
   */
  if (argc < 2) {
    fprintf(stderr, "Usage");
    exit(99);
  }

  /* Open the source file in read mode using Fopen */
  FILE *srcFile;
  srcFile = Fopen("srcFile.txt", "r");
  if (srcFile == NULL) {
    fprintf(stderr, "Cannot open srcFile.txt");
    exit(99);
  }
  /* Open a file in write mode using Fopen (make sure the file is overwritten if it already exists) */
  FILE *newFile;
  newFile = Fopen("dstFile.txt", "w");
  if (newFile == NULL) {
    fprintf(stderr, "Cannot open newFile.txt");
    exit(99);
  }

  /* Read a line at a time from the source file.
   *   If the line contains a comma-separated listing of a signed size_t, an unsigned size_t, 
   *   a short, and a string of at most 16 characters:
   *     write the contents of the line to the destination file in reverse order 
   *     (i.e., string first and signed size_t last), separated by tabs instead of commas
   */ 
  char *buffer = NULL;   
  size_t length = 0;   
  ssize_t numChars = 0;
  int i = 1;
  int filterdCount = 0;
  while ((numChars = getline(&buffer, &length, srcFile)) != -1) {
    ssize_t w;
    size_t x;
    short y;
    char z[16];  
    buffer[numChars-1] = '\0';
    
    int numMatches = sscanf(buffer, "%zd,%zu,%hd,%s", &w, &x, &y, z);
    if(numMatches > 0) {
      fprintf(newFile, "%d: %s\t%hd\t%zu\t%zd\n", i, z, y, x, w);
    } else {
      filterdCount++;
      fprintf(stderr, "Filtered line %d\n", i);
    }
    i++;
  } 
  /* Print to stdout the number of lines you filtered out */
  fprintf(stdout, "Filtered out %d lines\n", filterdCount);
  /* Close your files using Fclose */
  Fclose(srcFile);
  Fclose(newFile);
  /* Free any allocated memory (verify with "valgrind --leak-checks=yes readLine <source file> <destination") */
  if (buffer != NULL) {
    free(buffer);
  }
  return 0;
}


/* Q0: Why did the size_t print out 18446744073709551615 in passingTest2?
 * A0: size_t is an unsigned integer, so it can't be negative. So when reading the file, on passtedTest2, it read the -1 which is flipped to 2^64 -1 = 18446744073709551615.
 *
 * Q1: What did you learn from interestingTest1?
 * A1:  In interestingTest1, I learned that leading space in the size_t will be ignored. Therefore, extraspaces do not affect the result.
 *
 * Q2: What did you learn from interestingTest2?
 * A2: Similarly to intrestingTest1, leading and trailing space will be ignored. Therefore, extraspaces do not affect the result.
 *
 * Q3: Why do you think, in reallyVeryInterestingTest3, did reallyVeryInterestingTest3 
 *     print out in its entirety instead of just the 17 characters in the buffer?
 * A3: The buffer size is not 17 characters. The buffer is instead dependent on the numChars returned by getline. So the buffer size is actually 35 for reallyVeryInterestingTest3. Which allows for everything to be printed.
 *
 * Q4: Why are the second and third fields in the output of reallyVeryInterestingTest3
 *     not 99 and 99, respectively?
 * A4: Similary to Q1, 99 can't be represented with the 2nd and third fields. So the numbers are reprsented as 29541	13172 respectively.
 *
 * Q5: Why are you getting the last line printed out twice due to an empty line 8 in srcFile.txt?
 *     If you are still getting this duplication, take steps to correct this.  If you are not 
 *     getting this duplication, what did you do to prevent/correct this?
 * A5: I am not getting this duplication because I only printed out the line if the number of matches was greater than 0.
 *
 * Q6: Did you learn from the redirect of the stderr (using "2>| stderr.txt") that valgrind writes
 *     all its output to stderr?
 * A6: Yes, I learned that valgrind writes all its output to stderr. This includes the memory summary and the things printed to stderr. 
 *
 */
