package model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CsvToSpreadsheetConverterTest {

    @Test
    void when_function_coordinate_out_of_bounds_check_exception() {

        // create the Spreadsheet object with out-of-bounds coordinates for a sum function
        List<List<String>> functionIndexOutOfBoundsCsvValues = new ArrayList<>(Arrays.asList(
                Arrays.asList("1.4", "3.0", "#(sum A3 A4 A5)")
        ));

        //check that we are throwing a RuntimeException specifically
        Exception exception = assertThrows(RuntimeException.class, () ->
            new Spreadsheet(functionIndexOutOfBoundsCsvValues)
        );

        //Check the message coming back to make sure our process to handle out of bounds coordinates is working as expected.
        assertTrue(exception.getMessage().startsWith("A coordinate in this function:"),
                "The exception message should start with \"A coordinate in this function:\"");
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

    /*
        In the case of an unsupported function, in this case div, an error will be logged and then
        written to the sheet. In this case: "Function: div not supported"
        Below we check that no exceptions are thrown with an unsupported function.
     */
    @Test
    void when_function_not_supported_no_exception_thrown() {
        List<List<String>> unsupportedFunctionCsvValues = new ArrayList<>(Arrays.asList(
                Arrays.asList("1.5", "3.0", "#(div A1 B1)")
        ));
        Spreadsheet spreadsheet = new Spreadsheet(unsupportedFunctionCsvValues);
    }

    // This tests scanning of cells to perform functions on unprocessed functions further down the 2D array
    @Test
    void when_function_result_still_to_be_calculated_process_until_complete() {
        List<List<String>> deferredFunctionToProcess = new ArrayList<>(Arrays.asList(
                Arrays.asList("1.5", "3.0", "#(sum A1 C2)"),
                Arrays.asList("2.5", "4.0", "#(sum A2 B2)")
        ));

        //Check that the deferred function at C1 is calculated once the function at C2 has been calculated and that the result is correct.
        List<List<String>> spreadsheet = new Spreadsheet(deferredFunctionToProcess).getSpreadsheetCells();
        assertEquals("8.0", spreadsheet.get(0).get(2));
    }
}
