package parse;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Assert.assertEquals(expression,same);
        Assert.assertEquals(expression,expression);
        Assert.assertNotEquals(expression,different);
        Assert.assertNotEquals(expression,shorter);
        Assert.assertNotEquals(expression,longer);
    }

    @Test
    void toStringTest() {
        Assert.assertEquals("4Let",expression.toString());
        Assert.assertEquals("Let,Num,2Let",different.toString());
        Assert.assertEquals("3Let",shorter.toString());
        Assert.assertEquals("5Let",longer.toString());
    }
}