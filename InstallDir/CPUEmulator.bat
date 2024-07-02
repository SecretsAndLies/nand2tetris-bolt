@echo off
cd ..
mvn install -DskipTests
java -jar CPUEmulator\target\CPUEmulator-2.5-SNAPSHOT.jar