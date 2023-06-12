CC = gcc
CFLAGS = -g -Wall -Wextra -Wwrite-strings
LFLAGS = -g

which: which.o wrappers.o
	${CC} ${CFLAGS} -o which which.o wrappers.o
which.o: which.c wrappers.h
	${CC} ${CFLAGS} -c which.c -o which.o
wrappers.o: wrappers.c
	${CC} ${CFLAGS} -c wrappers.c -o wrappers.o
clean:
	rm -f *.o
	rm -f which