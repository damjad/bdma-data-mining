#!/bin/bash

rm -rf out/
mkdir -p out/
javac -source 1.8 -target 1.8 -d out/ `find src/ -name "*.java"`
echo "Build process ended"