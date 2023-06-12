all :
	gcc -c main.c 
	gcc -c function.c
	ar rs libmy_function.a function.o
	gcc main.o -L. -lmy_function -o main
clean:
	rm -f *.o *.a main