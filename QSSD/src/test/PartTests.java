package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import parse.Part;

public class PartTests {
    Part c,n,s,sy;

    @BeforeEach
    public void initialize() {
        c = new Part(76,0);
        n = new Part(78,0);
        s = new Part(83,0);
        sy = new Part(35,0);
    }

    @Test
    void equals() {
        Part[] parts = {c,n,s,sy};
        for (int i = 0; i < parts.length; i++) {
            Part p1 = parts[i];
            for (int y = 0; y < parts.length; y++) {
                Part p2 = parts[y];
                if (i == y) {
                    assertEquals(p1,p2);
                } else {
                    assertNotEquals(p1,p2);
                }
            }
        }
    }

    @Test
    void compare() {
        Part[] parts = {c,n,s,sy};
        for (int i = 0; i < parts.length; i++) {
            Part p1 = parts[i];
            for (int y = 0; y < parts.length; y++) {
                Part p2 = parts[y];
                if (i == y) {
                    assertEquals(0,p1.compare(p2));
                } else {
                    assertNotEquals(0,p1.compare(p2));
                }
            }
        }
        Part c2 = new Part(76,2,2);
        Part combined = new Part(c,c2);
        assertEquals(1,c.compare(combined));
        assertEquals(1,c.compare(c2));
    }

    @Test
    void MultiParts() {
        Part c2 = new Part(76,2,2);
        Part combined = new Part(c,c2);
        assertNotEquals(c,combined);
    }
}