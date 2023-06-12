all: main.o libmylib.a
	gcc main.o -L. -lmylib -o main
main.o:
	cpp -I../homework-2/include src/bin/main.c -o main.i
	cc -S main.i -o main.s
	as main.s -o main.o
libmylib.a:
	cpp src/mylib1.c -o mylib1.i
	cpp src/mylib2.c -o mylib2.i
	cc -S mylib1.i -o mylib1.s
	cc -S mylib2.i -o mylib2.s
	as mylib1.s -o mylib1.o
	as mylib2.s -o mylib2.o
	ar rcs libmylib.a mylib1.o mylib2.o
clean:
	rm -f *.i  *.s *.a *.o main