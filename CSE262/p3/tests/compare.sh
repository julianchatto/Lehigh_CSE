#!/bin/bash

langs=("java" "python")
tests=("parse")

# Print instructions on using this script
function usage() {
    echo "usage: ./compare.sh <lang> <test>"
    echo -n " <lang>:"
    for l in ${langs[@]}; do echo -n " $l"; done; echo
    echo -n " <test>:"
    for l in ${tests[@]}; do echo -n " $l"; done; echo
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

# Run tests
for f in `ls $2/*.xml`
do 
    echo `basename $f`
    diff -w ../results/$2/$1/`basename $f` ../results/$2/solutions/`basename $f`
done
