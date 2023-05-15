#ifndef SYSCALLS_H
#define SYSCALLS_H

/*
 * File: syscalls.h
 * Name: Mark Erle
 * UID:  merle
 */

#include <sys/socket.h>    /* for socklen_t */

pid_t Fork();
int Socketpair(int, int, int, int []);
pid_t Wait(int *);
ssize_t Write(int, const void *, size_t);
ssize_t Read(int, void *, size_t);
int Close(int);

#endif
