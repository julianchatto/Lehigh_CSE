all:
	gcc -c src/bin/main.c -I./include -o main.o
	gcc -c src/function.c -o function.o
	ar rs libmy_function.a function.o
	gcc main.o -L. -lmy_function -o main
clean:
	rm -f *.o *.a main

