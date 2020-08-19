package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import parse.Expression;

class ExpressionTests {
    Expression expression,same,different,shorter,longer;

    @BeforeEach
    public void initialize() {
        expression = new Expression("data");
        same = new Expression("boat");
        different = new Expression("d3ta");
        shorter = new Expression("dat");
        longer = new Expression("dataa");
    }

    @Test
    void equals() {
        assertEquals(expression,same);
        assertEquals(expression,expression);
        assertNotEquals(expression,different);
        assertNotEquals(expression,shorter);
        assertNotEquals(expression,longer);
    }

    @Test
    void toStringTest() {
        assertEquals("4Let",expression.toString());
        assertEquals("Let,Num,2Let",different.toString());
        assertEquals("3Let",shorter.toString());
        assertEquals("5Let",longer.toString());
    }
}