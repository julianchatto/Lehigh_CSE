CFLAGS=-g -c -Wall -Wextra -Wwrite-strings
LFLAGS=-g

lab7: lab7.o syscalls.o
	gcc ${LFLAGS} -o lab7 syscalls.o lab7.o
lab7.o: syscalls.h lab7.c
	gcc ${CFLAGS} lab7.c
syscalls.o: syscalls.c
	gcc ${CFLAGS} syscalls.c
clean:
	rm -f *.o
	rm -f lab7

