#include <stdio.h>       /* for printf */
#include <stdlib.h>      /* for exit */
#include <unistd.h>      /* for pipe, fork, read, write, close */
#include <errno.h>       /* for errno */
#include <string.h>      /* for strlen, strerror */
#include <time.h>        /* for nanosleep */

int Pipe(int pfd[2]) {
  if (pipe(pfd) == -1) {
    fprintf(stderr, "Pipe error %d - %s", errno, strerror(errno));
    exit(errno);
  }
  return 0;
}

int Nanosleep(const struct timespec *req, struct timespec *rem) {
  if (nanosleep(req, rem) == -1) {
    fprintf(stderr, "Nanosleep error %d - %s", errno, strerror(errno));
    exit(errno);
  }
  return 0;
}

pid_t Fork(void) {
  pid_t pid;
  if ((pid = fork()) == -1) {
    fprintf(stderr, "Fork error %d - %s", errno, strerror(errno));
    exit(errno);
  }
  return pid;
}

int Close(int pfd) {
  if (close(pfd) == -1) {
    fprintf(stderr, "Close error %d - %s", errno, strerror(errno));
    exit(errno);
  }
  return 0;
}

ssize_t Write(int pfd, const void *buffer, size_t nbytes) {
  ssize_t numBytes;
  if ((numBytes = write(pfd, buffer, nbytes)) == -1) {
    fprintf(stderr, "Write error %d - %s", errno, strerror(errno));
    exit(errno);
  }
  return numBytes;
}

ssize_t Read(int pfd, void *buffer, size_t nbytes) {
  ssize_t numBytes;
  if ((numBytes = read(pfd, buffer, nbytes)) == -1) {
    fprintf(stderr, "Read error %d - %s", errno, strerror(errno));
    exit(errno);
  }
  return numBytes;
}

