package parse;

import files.ExcelFile;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class ParseTableTests {
    ParseTable table;
    ArrayList<ArrayList<Object>> content;

    @BeforeEach
    void initialize() {
        this.content = new ArrayList<>(
                Arrays.asList(
                        new ArrayList<>(Arrays.asList("Id","Name","Surname")),
                        new ArrayList<>(Arrays.asList(0,"Ben","Ramsay Foster")),
                        new ArrayList<>(Arrays.asList(1,"William","Ambrose")),
                        new ArrayList<>(Arrays.asList(2,"Terry","Grover")),
                        new ArrayList<>(Arrays.asList(3,"Josh","Bryant")),
                        new ArrayList<>(Arrays.asList(4,"Nick","Pope"))
                )
        );
        this.table = new ParseTable(this.content);
    }

    @Test
    void testCountingMethods() {
        Assert.assertEquals(this.table.getRowCount(),5);
        Assert.assertEquals(this.table.getColumnCount(),3);
    }

    @Test
    void testHeaderNames() {
        Assert.assertArrayEquals(this.content.get(0).toArray(),this.table.getHeaderNames());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(this.content.get(0).get(i),this.table.getColumnName(i));
        }
    }

    @Test
    void testRowContents() {
        ArrayList<Object> expected, actual;
        for (int i = 1; i <= 5; i++) {
            expected = this.content.get(i);
            actual = this.table.getRow(i-1);
            for (int y = 0; y < expected.size(); y++) {
                Assert.assertEquals(expected.get(y),actual.get(y));
            }
        }
    }

    @Test
    void testGetColumnIdFromName() {
        for (int i = 0; i < this.content.get(0).size(); i++) {
            Assert.assertEquals(i,this.table.getColIdFromName(this.content.get(0).get(i).toString()));
        }
    }

    @Test
    void testCompositeTableMerge() {
        ArrayList<ParseTable> tables = new ExcelFile("./testing/Composite key tables.xlsx").getTables();
        ParseTable result = new ParseTable(tables.get(0),tables.get(1));

        ParseTable expected = new ExcelFile("./testing/Results/Composite key tables results.xlsx").getTables().get(0);
        Assert.assertEquals(result, expected);
    }

    @Test
    void testConcactenateTableMerge() {
        ArrayList<ParseTable> tables = new ExcelFile("./testing/Concactenate.xlsx").getTables();
        ParseTable result = new ParseTable(tables.get(0),tables.get(1));

        ParseTable expected = new ExcelFile("./testing/Results/Concactenate result.xlsx").getTables().get(0);
        Assert.assertEquals(result,expected);
    }

    @Test
    void testFiveTableMerge() {
        ArrayList<ParseTable> tables = new ExcelFile("./testing/5 Table merge.xlsx").getTables();
        ParseTable result = new ParseTable(tables.get(4),tables.get(0));
        result = new ParseTable(result,tables.get(1));
        result = new ParseTable(result,tables.get(2));
        result = new ParseTable(result,tables.get(3));

        ParseTable expected = new ExcelFile("./testing/Results/5 table merge result.xlsx").getTables().get(0);
        Assert.assertEquals(result, expected);
    }

    @Test
    void testMultipleOccurrencesMerge() {
        ArrayList<ParseTable> tables = new ExcelFile("./testing/Multiple Occurrences.xlsx").getTables();
        ParseTable result = new ParseTable(tables.get(0),tables.get(1));

        ParseTable expected = new ExcelFile("./testing/Results/Multple Occurrences result.xlsx").getTables().get(0);
        Assert.assertEquals(result,expected);
    }

    @Test
    void testTableWithOverlap() {
        ArrayList<ParseTable> tables = new ExcelFile("./testing/Tables with overlap.xlsx").getTables();
        ParseTable result = new ParseTable(tables.get(0),tables.get(1));

        ParseTable expected = new ExcelFile("./testing/Results/Tables with overlap result.xlsx").getTables().get(0);
        Assert.assertEquals(result,expected);
    }
}