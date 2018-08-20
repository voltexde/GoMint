#!/bin/sh
CXX="g++ -s -shared -fPIC -O3 -Wall -Werror -I$JAVA_HOME/include/ -I$JAVA_HOME/include/linux/"

echo $CXX
$CXX src/main/c/zlib.cpp -o src/main/resources/zlib.so -lz
$CXX src/main/c/hash.cpp -o src/main/resources/hash.so -lcrypto
