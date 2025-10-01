#!/bin/sh
cd ..
mvn install -DskipTests
java -jar CPUEmulator/target/CPUEmulator-2.6.1.jar