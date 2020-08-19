package parse.problems;

import java.util.ArrayList;

public class MixedTypes extends Problem {
    public MixedTypes(int col, ArrayList<Integer> row, String name) {
        this.Severe = false;
        this.title = name + " is nearly single type";
        this.description = "The majority of values are one type, but some are of a different type";
        this.column = col;
        this.rows = row;
    }
}
