Instructions to build and run:

In the pom directory, run the following commands:

    mvn clean install

    mvn exec:java -Dexec.args="src/main/resources/data.csv  src/main/resources/sheet.txt"

The csv filename can be changed on the command line to test other files.