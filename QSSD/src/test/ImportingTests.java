package test;

import files.CSVFile;
import files.ExcelFile;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import parse.ParseTable;

import java.util.ArrayList;

class ImportingTests {
    ExcelFile excelFile;
    CSVFile csvFile;
    ArrayList<ParseTable> tables;

    @Test
    void MultipleSheetImport() {
        excelFile = new ExcelFile("./testing/5 Table merge.xlsx");
        tables = excelFile.getTables();
        assertEquals(5,tables.size());
    }

    @Test
    void MultipleTablesSingleSheet() {
        excelFile = new ExcelFile("./testing/threeTables one sheet.xlsx");
        tables = excelFile.getTables();
        assertEquals(3,tables.size());
    }

    @Test
    void TestCsvImport() {
        csvFile = new CSVFile("./testing/Table1.csv");
        tables = csvFile.getTables();
        assertEquals(1,tables.size());
    }
}