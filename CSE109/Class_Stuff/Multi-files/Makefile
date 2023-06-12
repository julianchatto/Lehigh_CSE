all: 
	gcc main.c -c
	gcc my_lib.c -c
	ar rs libmy_lib.a my_lib.o
	gcc main.o -lmy_lib -L.
clean: 
	rm -rf *.o 
	rm -rf *.a
	rm -rf *.out