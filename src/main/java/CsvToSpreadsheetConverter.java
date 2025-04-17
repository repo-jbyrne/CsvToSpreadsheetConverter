import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvToSpreadsheetConverter {

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
            processSpreadsheet(spreadsheet);
//            writeSpreadSheet(spreadsheet);
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

    private static void processSpreadsheet(List<List<String>> spreadSheet) throws IOException {

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

        for (int i = 0; i < spreadSheet.size(); i++) {

            List<String> rowData = spreadSheet.get(i);

            for (int j = 0; j < rowData.size(); j++) {
                String cellValue = rowData.get(j);

                System.out.println("Cell value: " + cellValue);

                if (rowData.get(j).matches(".*[^0-9].*") && cellValue.startsWith("#(")) { // check if this cell is a function
                                                                                                //TODO I'm making the assumption that cellValue.startsWith("#(") is a good check for a function.
                    System.out.println("Function cell: " + cellValue);
                    cellValue = performFunction(rowData.get(j), spreadSheet);
                }

                rowData.set(j, cellValue); //TODO should these values in the 2D array be immutable? Then we have to create a new 2D array with the updated values
            }
            System.out.println();
        }

        System.out.println("SpreadSheet:");
        for (int i = 0; i < spreadSheet.size(); i++) {
            List<String> rowData = spreadSheet.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                System.out.print(rowData.get(j) + "|");
            }
            System.out.println();
        }
    }

    private static String performFunction(String functionCell, List<List<String>> spreadSheet) {

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

        Arrays.stream(parameters)
                .forEach(stringParameter ->
                    numericParameters.add(getNumericValueForKey(stringParameter, spreadSheet)));


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

        //throw error if any of the fields to perform a calc on contain characters
        //throw error if command is trying to access fields out of bounds of the rows or columns length
//        char letter = (char) ('A' + i - 1);
    }

    private static Double getNumericValueForKey(String stringParameter, List<List<String>> spreadsheet) {

        System.out.println("stringParameter: " + stringParameter);
        //TODO Double.parseDouble() throws a runtimeexception, should I handle this unchecked exception?

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

    //TODO NB Unit Tests
}
