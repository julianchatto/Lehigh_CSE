#ifndef SYSCALLS_H
#define SYSCALLS_H

/*
 * File: syscalls.h
 * Name: Mark Erle
 * UID:  mae5
 */

void *Malloc(size_t);

void Free (void *);

int Access(const char *, int);

FILE *Fopen(const char *, const char *);

void Fclose(FILE *);

#endif
