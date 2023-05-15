#include <stdio.h>   /* for fprintf */
#include <stdlib.h>  /* for size_t, malloc, exit */
#include <errno.h>   /* for errno */
#include <string.h>  /* for strerror */
#include <unistd.h>


void *Malloc(size_t size) {
  void *ptr = NULL; 
  if ((ptr = malloc(size)) == NULL) {  
    fprintf(stderr, "Could not allocated space for %ld bytes - %s", size, strerror(errno));
    exit(errno);
  }
  return ptr;

}

void Free (void *ptr) {
  if (!ptr) {
    fprintf(stderr, "Passed NULL pointer to free(void *ptr)\n");
    exit(255);
  } else {
    free(ptr);
  }

}

int Access(const char *pathname, int mode) {
  int rc = 0;
  if ((rc = access(pathname, mode)) != 0)
    fprintf(stderr, "%s not found or is not of mode %d\n", pathname, mode);
  return rc;
}

FILE *Fopen(const char *pathname, const char *mode) {
  FILE *fp = NULL;
  if ((fp = fopen(pathname, mode)) == NULL) {
    fprintf(stderr, "%s not found or is not of mode %d\n", pathname, mode);
  } 
  return fp;
  
}

void Fclose(FILE *stream) {
  int ret = 0;
  if((ret = fclose(stream)) != 0) {
    fprintf(stderr, "Error closing file stream\n");
  }
}