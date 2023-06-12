#ifndef SYSCALLS_H
#define SYSCALLS_H

int Pipe(int fildes[2]);
int Nanosleep(const struct timespec *req, struct timespec *rem);
pid_t Fork(void);
int Close(int fildes);
ssize_t Write(int fildes, const void *buf, size_t nbyte);
ssize_t Read(int fildes, void *buf, size_t nbyte);

#endif
