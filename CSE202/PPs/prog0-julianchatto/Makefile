all: prog0 move
	
prog0: prog0.o libuser.a
	gcc -o prog0 prog0.o -L. -luser
prog0.o:
	gcc -c ./src/bin/prog0.c -I include
libuser.a: user.o 
	ar rcs libuser.a user.o
user.o: 
	gcc -c ./src/user.c -I include
move: 
	mkdir -p build/lib/release
	mkdir -p build/objects
	mkdir -p build/bin
	mv prog0 build/bin
	mv *.o build/objects
	mv *.a build/lib/release
clean:
	rm -rf build
