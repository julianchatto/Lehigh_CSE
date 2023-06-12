#include <stdio.h>  /* for fprintf and getline */
#include <stdlib.h> /* for free */
#include <string.h> /* for strlen */

// ssize_t getline(char **lineptr, size_t *n, FILE *stream);
// int sscanf(const char *str, const char *format, ...);

int main() {

  char *buffer = NULL;   /* hold the string read */
  size_t length = 0;     /* length of the string read */
  ssize_t numChars = 0;  /* return val from getline */
  
  fprintf(stdout, "\nEnter some input (Cntl+D to exit): ");
  while ((numChars = getline(&buffer, &length, stdin)) != -1) {
    /* if numChars == -1, there was an error OR user entered Cntl+D (same as EOF) */
    /* if numChars == 1, user just hit Enter */
    /* if numChars > 1, user entered 1 or more characters followed by Enter */
    /* if numChars == 0, IDK how this would happen */

    fprintf(stderr, "\n\tnumChars    = %zd\n", numChars);
    fprintf(stderr,   "\tlength      = %zu\n", length);
    fprintf(stderr,   "\tbuffer len  = %zu\n", strlen(buffer));
    fprintf(stderr,   "\tbuffer      = \"%s\"\n", buffer);

    int numMatches; /* hold # of matches */
    char c; /* hold char */
    long l; /* hold long */
    int i; /* hold int */
    
    buffer[numChars - 1] = '\0';
    numMatches = sscanf(buffer, "%c %lu %d", &c, &l, &i);
    switch (numMatches) {
      case 3:
        fprintf(stderr, "got %d matches: %c %ld %d\n", numMatches, c, l, i);
        break;
      case 2:
        fprintf(stderr, "got %d matches: %c %ld %d\n", numMatches, c, l, i);
        break;
      case 1:
        fprintf(stderr, "got %d match:   %c %ld %d\n", numMatches, c, l, i);
        break;
      default:
        fprintf(stderr, "got no match or error, return value of sscanf is %d\n", numMatches);
        break;
    }

    fprintf(stdout, "\nEnter some input (Cntl+D to exit): ");
  }
  if (buffer) free(buffer);  /* can't forget to free the allocated memory! */

  return 0;
}
