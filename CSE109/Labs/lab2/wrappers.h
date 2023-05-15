#ifndef WRAPPERS_H
#define WRAPPERS_H
#include <stdlib.h>

void *Malloc(size_t size);

void *Free(void *var);

int Access(const char *pathname, int mode);
#endif
