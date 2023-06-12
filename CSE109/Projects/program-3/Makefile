g++ = g++ -Wall -Wextra -Wwrite-strings -Werror
makeBuild = mkdir -p build/lib/release; mkdir -p build/objects
moveOA = mv *.o build/objects; mv *.a build/lib/release
moveOS = mv *.o build/objects; mv *.so build/lib/release

all: static shared test # build static and shared libraries.
	build/bin/test
static: src/lib.cpp # build a static library `libhashset.a`, put it in `build/lib/release`. Put object files in `build/objects`
	${g++} -c src/lib.cpp -I include
	${g++} -c src/LinkedList.cpp -I include
	ar rcs libhashset.a *.o
	${makeBuild}
	${moveOA}
shared: src/lib.cpp # build a shared library `libhashset.so`, put it in `build/lib/release`. Put object files in `build/objects`
	${g++} -fPIC -c src/lib.cpp src/LinkedList.cpp -I include
	${g++} -shared -o libhashingShare.so *.o
	${makeBuild}
	${moveOS}
debug: src/lib.cpp # build a shared library with debug symbols, put it in `build/lib/debug`. Put object files in `build/objects`
	${g++} -g -fPIC -c src/lib.cpp src/LinkedList.cpp -I include
	${g++} -g -shared -o libhashingShare.so *.o
	${makeBuild}
	mkdir -p build/lib/debug
	mv *.o build/objects; mv *.so build/lib/debug
clean: # remove all build artifacts by removing the build directory.
	rm -rf build
install: # move the shared library to `/usr/lib`
	mv build/lib/release/libhashingShare.so /usr/lib
test: tests/test.cpp # compile `tests/test.cpp` and run it. Put the tests executable in `build/bin`. Put object files in `build/objects`
	${g++} -c tests/test.cpp -I include
	${g++} -o test test.o -lhashset -L build/lib/release
	mkdir -p build/bin
	mv test build/bin
	${makeBuild}
	mv *.o build/objects