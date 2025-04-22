package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spreadsheet {
    private List<List<String>> spreadsheetCells;
    private Integer maxCellLength = 0;

    /*
        The spreadsheet values can only be set once. The only way to create the spreadsheet 2D array is through the
        constructor which will only run processSpreadsheet() once.

        We set the internal spreadsheetCells 2D array using the csvValues passed into the constructor.
        The internal spreadsheetCells 2D array fields are updated themselves.
     */
    public Spreadsheet(List<List<String>> csvValues) {

        spreadsheetCells = csvValues;
        processSpreadsheet();
    }

    public List<List<String>> getSpreadsheetCells() {
        return spreadsheetCells;
    }

    private void processSpreadsheet() {

        boolean processingComplete = false; //initialise as false so that the while loop can start
        System.out.println("processingComplete initial state: " + processingComplete);

        while (!processingComplete) {
            for (int i = 0; i < spreadsheetCells.size(); i++) {

                List<String> rowData = spreadsheetCells.get(i);
                processingComplete = true; // set processingComplete to true, it gets set to false if a function cell is
                // found. We scan through the csvValues until all functions are complete
                System.out.println("processingComplete: " + processingComplete);

                for (int j = 0; j < rowData.size(); j++) {

                    String cellValue = rowData.get(j);

                    System.out.println("Cell value: " + cellValue);

                    // check if this cell is a function, if yes, update the cellValue with the function result
                    if (rowData.get(j).matches(".*[^0-9].*") && cellValue.startsWith("#(")) {

                        //I'm making the assumption that cellValue.startsWith("#(") is a good check for a function.
                        processingComplete = false;
                        System.out.println("processingComplete: " + processingComplete);
                        System.out.println("Function cell: " + cellValue);

                        try {
                            cellValue = performFunction(rowData.get(j));
                        } catch(IndexOutOfBoundsException e) {
                            //catch the exception and log an error, the cellValue stays as it is
                            System.out.println("A coordinate in this function: " + rowData.get(j) + " is out of bounds for the spreadsheet. The spreadsheet cannot be processed");
                            throw new RuntimeException("A coordinate in this function: " + rowData.get(j) + " is out of bounds for the spreadsheet. Spreadsheet cannot be processed");
                        }
                    }

                    //update maxCellLength if the current cellValue is larger than the maxCellLength
                    maxCellLength = maxCellLength < cellValue.length() ? cellValue.length() : maxCellLength;

                    rowData.set(j, cellValue);
                }
            }
        }
    }

    private String performFunction(String functionCell) {

        //Parse Function cell
        String[] functionAndCellCoordinates = functionCell.substring(functionCell.indexOf("(")+1, functionCell.indexOf(")")).split(" ");

        String function = functionAndCellCoordinates[0];
        String[] cellCoordinates = Arrays.copyOfRange(functionAndCellCoordinates, 1, functionAndCellCoordinates.length);

        List<Double> numericCellValues = new ArrayList<>();

        try {
            Arrays.stream(cellCoordinates)
                    .forEach(cellCoordinate ->
                            numericCellValues.add(getNumericValueForCellCoordinate(cellCoordinate)));
        } catch(NumberFormatException e) {
            return functionCell; //if a parameter of the function is non-numeric we get a NumberFormatException
            //we return the original function string and allow it to be calculated later when
            //the field it references has been calculated.
        }

        Double result = 0.0;

        switch (function.toLowerCase()) {
            case "prod":
                result = numericCellValues.stream()
                        .reduce(1.0, (productResult, parameter) -> productResult * parameter);
                break;
            case "sum":
                result = numericCellValues.stream()
                        .reduce(0.0, Double::sum);
                break;
            default:
                System.out.println("Unsupported function: " + function);
                return "Function: " + function.toLowerCase() + " not supported";
        }

        return String.valueOf(result);
    }
    private Double getNumericValueForCellCoordinate(String cellCoordinate) {

        System.out.println("cellCoordinate: " + cellCoordinate);

        char letter = cellCoordinate.charAt(0);
        Integer number = Integer.parseInt(cellCoordinate.substring(1))-1;
        Integer letterIndex = (letter - 'A');

        return Double.parseDouble(spreadsheetCells.get(number).get(letterIndex));
    }

    public void writeToFile(String outputSpreadSheetFilePath) {

        try (FileWriter writer = new FileWriter(outputSpreadSheetFilePath)) {
            for (int i = 0; i < spreadsheetCells.size(); i++) {
                List<String> rowData = spreadsheetCells.get(i);
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