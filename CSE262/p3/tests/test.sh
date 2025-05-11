#!/bin/bash

langs=("java" "python")
tests=("parse")

# Print instructions on using this script
function usage() {
    echo "usage: ./test.sh <lang> <test>"
    echo -n " <lang>:"; for l in ${langs[@]}; do echo -n " $l"; done; echo
    echo -n " <test>:"; for l in ${tests[@]}; do echo -n " $l"; done; echo
}

# Command-line argument validation functions
function check_lang() {
    for l in ${langs[@]}; do if [ "$1" == "$l" ]; then return; fi; done
    usage; exit;
}
function check_test() {
    for t in ${tests[@]}; do if [ "$1" == "$t" ]; then return; fi; done
    usage; exit;
}

# Validate
check_lang $1
check_test $2

# Clear the output folder
rm -rf ../results/$2/$1
mkdir -p ../results/$2/$1

# Build the binary and get the execution command
cmd=""
if [ "$1" == "cc" ]; then
    make -C ../src/cc
    cmd="../src/cc/obj64/slang.exe"
fi
if [ "$1" == "go" ]; then
    cd ../src/go && go build & cd ../tests
    cmd="../src/go/slang"
fi
if [ "$1" == "java" ]; then
    cd ../src/java && ./gradlew build && cd ../../tests
    cmd="java -jar ../src/java/app/build/libs/app.jar"
fi
if [ "$1" == "python" ]; then
    cmd="python3 ../src/python/slang.py"
fi
if [ "$1" == "rust" ]; then
    cd ../src/rust && cargo build && cd ../../tests
    cmd="../src/rust/target/debug/slang"
fi
if [ "$1" == "typescript" ]; then
    cd ../src/typescript && npm install && npm run build && cd ../../tests
    cmd="node ../src/typescript/dist/slang.js"
fi

# Run the tests
for t in `ls $2/*.xml`
do
    echo $t; $cmd -$2 $t > ../results/$2/$1/`basename $t`
done
