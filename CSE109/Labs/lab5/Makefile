filterFile:
	gcc -c filterFile.c
	gcc -c syscalls.c
	ar rs libmy_syscalls.a syscalls.o
	gcc filterFile.o -L. -lmy_syscalls -o filterFile
clean:
	rm -f *.a *.o filterFile