import model.Spreadsheet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CsvToSpreadsheetConverter {

    //TODO NB Unit Tests
    //TODO NB Use maven to build and run from the command line

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Please specify the input csv file to use and the output text file for the spreadsheet");
            System.out.println("Usage: java -cp . CsvToSpreadsheetConverter <input.csv> <output.txt>");
            return;
        }

        String inputCsvFilePath = args[0];
        String outputSpreadSheetFilePath = args[1];

        try {
            Spreadsheet spreadsheet = new Spreadsheet(readCsv(inputCsvFilePath));
            spreadsheet.writeToFile(outputSpreadSheetFilePath);
        } catch (IOException e) {
            // If this exception is thrown, there was an error reading in the CSV file, the process can't be run.
            // So we log the message and complete.
            System.out.println("Unable to read the CSV file \n" + e.getMessage());
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
}
