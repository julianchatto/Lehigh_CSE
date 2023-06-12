lab8: lab8.o syscalls.o
	gcc -g -o lab8 lab8.o syscalls.o

lab8.o: lab8.c syscalls.h
	gcc -g -c -Wall -Wextra -Wwrite-strings lab8.c

syscalls.o: syscalls.c syscalls.h
	gcc -g -c -Wall -Wextra -Wwrite-strings syscalls.c

clean:
	rm *.o
	rm lab8
