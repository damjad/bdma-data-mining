# Decision Tree with ID3

This project is done as a coding assignment for [data mining](http://cs.ulb.ac.be/public/teaching/infoh423) course at ULB.
This project contains the code for ID3 decision tree algorithm without the pruning.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 

### Prerequisites

* JDK 1.8

### Building
You can build the project either through scripts or manually running javac.

In the main directory of the project run the following:

#### Unix Based Systems

````
#!/bin/bash

rm -rf out/
mkdir -p out/
javac -source 1.8 -target 1.8 -d out/ `find src/ -name "*.java"`
````

Or you can run the script:

````
scripts/build.sh
````

#### Windows

````
rmdir out /s /Q
mkdir out
dir /s /B *.java > out/sources.txt
javac -source 1.8 -target 1.8 -d out "@out/sources.txt"

````

### Running
You can run the project either through scripts or manually running javac.

In the main directory of the project run the following:

Replace `<index>` with the column index starting from 0 and replace `<file-path>` with the path of the file.
The delimiter for csv can be configured through a System property _csv.delim_.

#### Unix Based Systems

````
java -Dcsv.delim=, -cp out com.danish.dm.Main <index> <file-path>
````

Or you can run the script:

````
scripts/run.sh <index> <file-path>
````

#### Windows

In the main directory of the project.

````
java -Dcsv.delim=, -cp out com.danish.dm.Main <index> <file-path>
````

## Authors

* **Danish Amjad** - *Initial work* - [Danish Amjad](https://github.com/damjad)