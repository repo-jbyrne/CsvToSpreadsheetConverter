import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvToSpreadsheetConverterTest {

    //TODO use mockito so we don't have to actually create any files
    //TODO test with incorrect filename
    //TODO test with array index out of bounds
    //TODO test with value at co-ordinate is string
    //TODO test working case

    private final List<List<String>> SPREAD_SHEET = new ArrayList<>();

    @Test
    void testPerformFunction() {
        //TODO should this be a spreadsheet object that has a perform function/s method?
        CsvToSpreadsheetConverter.performFunction("#(prod A4 B4)", SPREAD_SHEET);
    }
}
