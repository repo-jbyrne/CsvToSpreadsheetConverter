Instructions to build and run:

In the directory with the pom file in it, run the following commands. The first one runs the tests too.

    mvn clean install -DskipTests=false

    mvn exec:java -Dexec.args="src/main/resources/data.csv  src/main/resources/sheet.txt"

The csv filename can be changed on the command line to test other files.
Data2.csv has the second input example data in it:

mvn exec:java -Dexec.args="src/main/resources/data2.csv  src/main/resources/sheet2.txt"