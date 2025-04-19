import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CsvToSpreadsheetConverter {

    //TODO NB Unit Tests
    //TODO NB Use maven to build and run from the command line

    //    |  A  |  B  |  C
    // --------------------
    //  1 |
    // ---|
    //  2 |
    // ---|
    //  3 |
    // ---|
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Please specify the input csv file to use and the output text file for the spreadsheet");
            System.out.println("Usage: java -cp . CsvToSpreadsheetConverter <input.csv> <output.txt>");
            return;
        }

        String inputCsvFilePath = args[0];
        String outputSpreadSheetFilePath = args[1];

        try {
            List<List<String>> spreadsheet = readCsv(inputCsvFilePath);
            processSpreadsheet(spreadsheet, outputSpreadSheetFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<List<String>> readCsv(String filePath) throws IOException {
        List<List<String>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                List<String> row = Arrays.asList(values);
                data.add(row);
            }
        }
        return data;
    }

    private static void processSpreadsheet(List<List<String>> spreadSheet, String outputSpreadSheetFilePath) throws IOException {

        System.out.println("SpreadSheet:");
        for (int i = 0; i < spreadSheet.size(); i++) {
            List<String> rowData = spreadSheet.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                System.out.print(rowData.get(j) + "|");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();

        boolean processingComplete = false; //initialise as false so that the while loop can start
        System.out.println("processingComplete initial state: " + processingComplete);

        Integer maxCellLength = 0;

        while (!processingComplete) { //TODO three nested loops.
            for (int i = 0; i < spreadSheet.size(); i++) {

                List<String> rowData = spreadSheet.get(i);
                processingComplete = true; // set processingComplete to true, it gets set to false if a function cell is
                                            // found. We scan through the cells until all functions are complete
                System.out.println("processingComplete: " + processingComplete);

                for (int j = 0; j < rowData.size(); j++) {
                    String cellValue = rowData.get(j);

                    System.out.println("Cell value: " + cellValue);

                    if (rowData.get(j).matches(".*[^0-9].*") && cellValue.startsWith("#(")) { // check if this cell is a function
                        //TODO I'm making the assumption that cellValue.startsWith("#(") is a good check for a function.
                        processingComplete = false;
                        System.out.println("processingComplete: " + processingComplete);
                        System.out.println("Function cell: " + cellValue);
                        cellValue = performFunction(rowData.get(j), spreadSheet);
                    }

                    maxCellLength = maxCellLength < cellValue.length() ? cellValue.length() : maxCellLength;
                    rowData.set(j, cellValue); //TODO should these values in the 2D array be immutable? To protect against side effects
                                                //TODO Then we have to create a new 2D array with the updated values.
                }
                System.out.println();
            }
        }

        System.out.println("SpreadSheet:");
        for (int i = 0; i < spreadSheet.size(); i++) {
            List<String> rowData = spreadSheet.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                String output;
                if(rowData.get(j).trim().equalsIgnoreCase("#hl")) {
                    output = "-".repeat(maxCellLength) + "|";
                    System.out.print(output);
                } else {
                    boolean isNumber = false;
                    try {
                        Double.parseDouble(rowData.get(j));
                        isNumber = true;

                    } catch (NumberFormatException e){
                        //ignore
                    }

                    String padding = " ".repeat(maxCellLength - rowData.get(j).length());
                    if(isNumber) {
                        System.out.print(padding + rowData.get(j) + "|");
                    } else {
                        System.out.print(rowData.get(j) + padding + "|");
                    }
                }
            }
            System.out.println();

            writeSpreadSheet(spreadSheet, maxCellLength, outputSpreadSheetFilePath);
        }
    }

    static protected String performFunction(String functionCell, List<List<String>> spreadSheet) {

        //TODO check for null string or malformed function syntax

        //Parse Function cell
        String[] functionAndParameters = functionCell.substring(functionCell.indexOf("(")+1, functionCell.indexOf(")")).split(" ");

        System.out.println("functionAndParameters:");
        Arrays.stream(functionAndParameters).forEach(System.out::println);
        System.out.println();

        String function = functionAndParameters[0];
        String[] parameters = Arrays.copyOfRange(functionAndParameters, 1, functionAndParameters.length);

        System.out.println("Function: " + function);
        System.out.println("Parameters: ");
        Arrays.stream(parameters).forEach(System.out::println);
        System.out.println();

        List<Double> numericParameters = new ArrayList<>();

        try {
            Arrays.stream(parameters)
                    .forEach(stringParameter ->
                            numericParameters.add(getNumericValueForKey(stringParameter, spreadSheet)));
        } catch(NumberFormatException e) {
            return functionCell; //if a parameter of the function is non-numeric we get a NumberFormatException
                                    //we return the original function string and allow it to be calculated later when
                                    //the field it references has been calculated.
        }


        System.out.println("functionAndParameters: " + functionAndParameters);
        System.out.println("function: " + function);
        System.out.println("parameters: " + parameters);
        System.out.println("numericParameters: " + numericParameters);

        Double result = 0.0;

        switch (function.toLowerCase()) {
            case "prod":
                result = numericParameters.stream()
                        .reduce(1.0, (productResult, parameter) -> productResult * parameter);
                break;
            case "sum":
                result = numericParameters.stream()
                        .reduce(0.0, (sumResult, parameter) -> sumResult + parameter);
                break;
            default:
                System.out.println("Unsupported function: " + function);
        }

        System.out.println("Function: " + function + " Result: " + result);

        return String.valueOf(result);

        //TODO NB: throw error if command is trying to access fields out of bounds of the rows or columns length
    }

    private static Double getNumericValueForKey(String stringParameter, List<List<String>> spreadsheet) {

        System.out.println("stringParameter: " + stringParameter);

        char letter = stringParameter.charAt(0);
        System.out.println("letter: " + letter);
        String stringNumber = stringParameter.substring(1, stringParameter.length());
        System.out.println("stringNumber: " + stringNumber);
        Integer number = Integer.parseInt(stringNumber)-1;
        System.out.println("number: " + number);
        Integer letterIndex = (letter - 'A');
        System.out.println("letterIndex: " + letterIndex);
        System.out.println("Value at key position: " + spreadsheet.get(number).get(letterIndex));
        return Double.parseDouble(spreadsheet.get(number).get(letterIndex));
    }

    private static void writeSpreadSheet(List<List<String>> spreadSheet, Integer maxCellLength, String filename) {

        try (FileWriter writer = new FileWriter(filename)) {
            for (int i = 0; i < spreadSheet.size(); i++) {
                List<String> rowData = spreadSheet.get(i);
                for (int j = 0; j < rowData.size(); j++) {
                    String output;
                    if (rowData.get(j).trim().equalsIgnoreCase("#hl")) {
                        output = "-".repeat(maxCellLength) + "|";
                        writer.write(output);
                    } else {
                        boolean isNumber = false;
                        try {
                            Double.parseDouble(rowData.get(j));
                            isNumber = true;

                        } catch (NumberFormatException e) {
                            //ignore
                        }

                        String padding = " ".repeat(maxCellLength - rowData.get(j).length());
                        if (isNumber) {
                            writer.write(padding + rowData.get(j) + "|");
                        } else {
                            writer.write(rowData.get(j) + padding + "|");
                        }
                    }
                }
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
