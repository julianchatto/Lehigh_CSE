#!/bin/bash

# Define variables
SRC_DIR="juc226"
BIN_DIR="bin"
JAR_FILE="juc226.jar"
MANIFEST_FILE="Manifest.txt"
LIB_JAR="ojdbc11.jar"

# Step 1: Compile Java files
javac -d $BIN_DIR $SRC_DIR/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed. Exiting."
    exit 1
fi

# Step 2: Package into an executable JAR
jar cfm $JAR_FILE $MANIFEST_FILE -C $BIN_DIR .
if [ $? -ne 0 ]; then
    echo "Failed to create JAR file. Exiting."
    exit 1
fi

# Step 3: Run the JAR file
java -jar $JAR_FILE

