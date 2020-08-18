package test;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parse.ParseColumn;

class ParseColumnTests {
    ParseColumn column;

    @BeforeEach
    public void initialize() {
        column = new ParseColumn("Column",1);
    }

    @Test
    void Constructor() {
        Assert.assertEquals(0,column.size());
        Assert.assertEquals("Column",column.getName());
        Assert.assertEquals(1,column.getId());
        Assert.assertEquals(0,column.getProblems().size());
        Assert.assertTrue(column.isEmpty());
    }

    @Test
    void Normalize() {
        column.normalise(5);
        Assert.assertEquals(5,column.size());
    }

    @Test
    void addContent() {
        Object[] data = {"data",0,null,.5f,false,'F'};
        for (Object o: data) {
            column.addContent(o);
        }
        Assert.assertEquals(6,column.size());
    }

    @Test
    void setContent() {
        Object[] data = {"one","two","three","four"};
        for (Object o: data) {
            column.addContent(o);
        }
        Assert.assertEquals("two",column.get(1));
        column.set(1,"three");
        Assert.assertEquals("three",column.get(1));
    }

    @Test
    void swapContent() {
        Object[] data = {"one","two","three","four"};
        for (Object o: data) {
            column.addContent(o);
        }
        Assert.assertEquals("two",column.get(1));
        Assert.assertEquals("three",column.get(2));
        column.swap(1,2);
        Assert.assertEquals("two",column.get(2));
        Assert.assertEquals("three",column.get(1));
    }

    @Test
    void isEmpty() {
        Assert.assertTrue(column.isEmpty());
        column.addContent(null);
        Assert.assertTrue(column.isEmpty());
    }

    @Test
    void findRowByObject() {
        Object[] data = {"one","two","three","four"};
        for (Object o: data) {
            column.addContent(o);
        }
        Assert.assertEquals(-1,column.findRowByObject("five"));
        Assert.assertEquals(1,column.findRowByObject("two"));
    }

    @Test
    void performChecks() {
        column.performChecks();
        Assert.assertEquals("Empty",column.getAttributes());
        Object[] data = {"one","two","three","four"};
        for (Object o: data) {
            column.addContent(o);
        }
        column.performChecks();
        Assert.assertEquals("Unique,String",column.getAttributes());
        column.addContent("two");
        column.performChecks();
        Assert.assertEquals("String",column.getAttributes());
        column.addContent(2);
        column.performChecks();
        Assert.assertEquals("",column.getAttributes());
    }

    @Test
    void checkTypes() {
        column.addContent("one");
        column.performChecks();
        ParseColumn col2 = new ParseColumn("col2",2);
        col2.addContent("two");
        col2.performChecks();
        Assert.assertTrue(column.checkType(col2));
    }

    @Test
    void checkContent() {
        column.addContent(1);
        column.performChecks();
        ParseColumn col2 = new ParseColumn("col2",2);
        int[] values = {1,2,3,4,5};
        for (int i: values) {
            col2.addContent(i);
            Assert.assertArrayEquals(new int[]{100, Math.round(100f/ (float)i)},column.checkContent(col2));
        }
    }
}