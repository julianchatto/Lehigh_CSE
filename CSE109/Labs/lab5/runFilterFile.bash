#!/bin/bash

clear
make
if [[ $? == 0 ]]; then
  valgrind --leak-check=yes filterFile srcFile.txt dstFile.txt 2>|stderr.txt
  echo "srcFile.txt contents..."
  cat -n srcFile.txt
  echo "dstFile.txt contents..."
  cat -n dstFile.txt
  echo "stderr.txt contents..."
  cat -n stderr.txt
else
  echo "make failed!"
fi
