package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import parse.ParseColumn;

class ParseColumnTests {
    ParseColumn column;

    @BeforeEach
    public void initialize() {
        column = new ParseColumn("Column",1);
    }

    @Test
    void Constructor() {
        assertEquals(0,column.size());
        assertEquals("Column",column.getName());
        assertEquals(1,column.getId());
        assertEquals(0,column.getProblems().size());
        assertTrue(column.isEmpty());
    }

    @Test
    void Normalize() {
        column.normalise(5);
        assertEquals(5,column.size());
    }

    @Test
    void addContent() {
        Object[] data = {"data",0,null,.5f,false,'F'};
        for (Object o: data) {
            column.addContent(o);
        }
        assertEquals(6,column.size());
    }

    @Test
    void setContent() {
        Object[] data = {"one","two","three","four"};
        for (Object o: data) {
            column.addContent(o);
        }
        assertEquals("two",column.get(1));
        column.set(1,"three");
        assertEquals("three",column.get(1));
    }

    @Test
    void swapContent() {
        Object[] data = {"one","two","three","four"};
        for (Object o: data) {
            column.addContent(o);
        }
        assertEquals("two",column.get(1));
        assertEquals("three",column.get(2));
        column.swap(1,2);
        assertEquals("two",column.get(2));
        assertEquals("three",column.get(1));
    }

    @Test
    void isEmpty() {
        assertTrue(column.isEmpty());
        column.addContent(null);
        assertTrue(column.isEmpty());
    }

    @Test
    void findRowByObject() {
        Object[] data = {"one","two","three","four"};
        for (Object o: data) {
            column.addContent(o);
        }
        assertEquals(-1,column.findRowByObject("five"));
        assertEquals(1,column.findRowByObject("two"));
    }

    @Test
    void performChecks() {
        column.performChecks();
        assertEquals("Empty",column.getAttributes());
        Object[] data = {"one","two","three","four"};
        for (Object o: data) {
            column.addContent(o);
        }
        column.performChecks();
        assertEquals("Unique,String",column.getAttributes());
        column.addContent("two");
        column.performChecks();
        assertEquals("String",column.getAttributes());
        column.addContent(2);
        column.performChecks();
        assertEquals("",column.getAttributes());
    }

    @Test
    void checkTypes() {
        column.addContent("one");
        column.performChecks();
        ParseColumn col2 = new ParseColumn("col2",2);
        col2.addContent("two");
        col2.performChecks();
        assertTrue(column.checkType(col2));
    }

    @Test
    void checkContent() {
        column.addContent(1);
        column.performChecks();
        ParseColumn col2 = new ParseColumn("col2",2);
        int[] values = {1,2,3,4,5};
        for (int i: values) {
            col2.addContent(i);
            assertArrayEquals(new int[]{100, Math.round(100f/ (float)i)},column.checkContent(col2));
        }
    }
}