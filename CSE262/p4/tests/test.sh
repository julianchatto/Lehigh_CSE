#!/bin/bash

langs=("java" "python")
tests=("interpret")

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
if [ "$1" == "java" ]; then
    cd ../java && ./gradlew build && cd ../tests
    cmd="java -jar ../java/app/build/libs/app.jar"
fi
if [ "$1" == "python" ]; then
    cmd="python3 ../python/slang.py"
fi
# Run the tests
for t in `ls $2/*.xml`
do
    echo $t; $cmd -$2 $t > ../results/$2/$1/`basename $t`
done
