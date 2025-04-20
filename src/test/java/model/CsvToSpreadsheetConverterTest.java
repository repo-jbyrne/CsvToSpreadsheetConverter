package model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CsvToSpreadsheetConverterTest {

    //TODO run tests on mvn clean install

    //TODO test with value at function parameter co-ordinate is non-numeric -
    //  - this tests scanning of cells to perform functions on unprocessed functions further down the 2D array

    // create the Spreadsheet object with out-of-bounds coordinates for a sum function, check exception is
    //handled and no exception is thrown
    @Test
    void when_function_coordinate_out_of_bounds_check_exception() {

        List<List<String>> functionIndexOutOfBoundsCsvValues = new ArrayList<>(Arrays.asList(
                Arrays.asList("1.4", "3.0", "#(sum A3 A4 A5)")
        ));

        assertThrows(RuntimeException.class, () ->
            new Spreadsheet(functionIndexOutOfBoundsCsvValues)
        );

        //TODO can I work out how to assert exception message equals expected message
    }

    @Test
    void when_perform_sum_function_check_result_correct() {

        List<List<String>> functionSumCsvValues = new ArrayList<>(Arrays.asList(
                Arrays.asList("1.4", "3.0", "#(sum A1 B1)")
        ));
        Spreadsheet spreadsheet = new Spreadsheet(functionSumCsvValues);
        spreadsheet.getSpreadsheetCells();
        assertEquals("4.4", spreadsheet.getSpreadsheetCells().get(0).get(2));
    }

    @Test
    void when_perform_product_function_check_result_correct() {

        List<List<String>> functionSumCsvValues = new ArrayList<>(Arrays.asList(
                Arrays.asList("1.5", "3.0", "#(prod A1 B1)")
        ));
        Spreadsheet spreadsheet = new Spreadsheet(functionSumCsvValues);
        spreadsheet.getSpreadsheetCells();
        assertEquals("4.5", spreadsheet.getSpreadsheetCells().get(0).get(2));
    }

    @Test
    void testUnsupportedFunction() {
        List<List<String>> unsupportedFunctionCsvValues = new ArrayList<>(Arrays.asList(
                Arrays.asList("1.5", "3.0", "#(div A1 B1)")
        ));
        Spreadsheet spreadsheet = new Spreadsheet(unsupportedFunctionCsvValues);
    }

    @Test
    void testMalformedFunction() {
        List<List<String>> malformedFunctionCsvValues = new ArrayList<>(Arrays.asList(
                Arrays.asList("1.5", "3.0", "#(345")
        ));
        new Spreadsheet(malformedFunctionCsvValues);
    }

    @Test
    void testMalformedNumericField() {
        List<List<String>> malformedNumericFieldCsvValues = new ArrayList<>(Arrays.asList(
                Arrays.asList("1.5", "3.0", "2.5 error")
        ));
        new Spreadsheet(malformedNumericFieldCsvValues);
    }
}
