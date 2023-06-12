#include <stdio.h>   /* for fprintf */
#include <stdlib.h>  /* for size_t, malloc, exit */
#include <errno.h>   /* for errno */
#include <string.h>  /* for strerror */
#include <unistd.h>  /* for access */

void *Malloc(size_t size) {
  void *ptr = NULL; /* or = 0; initializing not really needed b/c ptr will always be given a value by malloc call */
  if ((ptr = malloc(size)) == NULL) {  /* error checking and handling */
    fprintf(stderr, "Could not allocated space for %ld bytes - %s", size, strerror(errno));
    exit(errno);
  }
  return ptr;
}

void *Free(void *ptr) {
  if (ptr == NULL) {
    fprintf(stderr, "NULL passed, cannot be freed\n");
    exit(255);
  } 
  free(ptr);
  return NULL;
}

int Access(const char *pathname, int mode) {
  if (pathname == NULL) {
    fprintf(stderr, "NULL patname, cannot be accessed\n");
    return 255;
  }
  return access(pathname, mode);

}
