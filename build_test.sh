#!/bin/bash

javac linda/*.java linda/shm/*.java linda/utils/*.java
jar -c linda/*.class linda/shm/*.class linda/utils/*.class > mylinda.jar
javac -classpath mylinda.jar TestVide.java
java -classpath .:mylinda.jar TestVide
rm **/*.class