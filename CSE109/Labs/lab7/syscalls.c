/*
 * File: syscalls.c
 * UserID: Ivanna B. Kewl
 */

#include <stdio.h>       /* for fprintf */
#include <stdlib.h>      /* for exit */
#include <errno.h>       /* for errno */
#include <string.h>      /* for strerror */
#include <unistd.h>      /* for pipe, fork, read, write, close */
#include <sys/wait.h>    /* for wait */
#include <sys/socket.h>  /* for socketpair */


pid_t Fork() {
    pid_t forked;
    if ((forked = fork()) < 0) {
        fprintf(stderr, "fork error: %s\n", strerror(errno));
        exit(errno);
    } 
    return forked;
}
int Socketpair(int domain, int type, int protoccol, int sv[]) {
    int socket;
    if ((socket = socketpair(domain, type, protoccol, sv)) < 0) {
        fprintf(stderr, "socketpair error: %s\n", strerror(errno));
        exit(errno);
    }
    return socket;
}
pid_t Wait(int *wstatus) {
    pid_t waits;
    if ((waits = wait(wstatus)) < 0) {
        fprintf(stderr, "wait error: %s\n", strerror(errno));
        exit(errno);
    }
    return waits;
}
ssize_t Write(int fd , const void *buf, size_t count) {
    ssize_t writes;
    if ((writes = write(fd, buf, count)) < 0) {
        fprintf(stderr, "write error: %s\n", strerror(errno));
        exit(errno);
    }
    return writes;
}
ssize_t Read(int fd, void *buf, size_t count) {
    ssize_t reads;
    if ((reads = read(fd, buf, count)) < 0) {
        fprintf(stderr, "read error: %s\n", strerror(errno));
        exit(errno);
    }
    return reads;
}
int Close(int fd) {
    int closed; 
    if ((closed = close(fd)) < 0) {
        fprintf(stderr, "close error: %s\n", strerror(errno));
        exit(errno);
    }
    return closed;
}