package parse.problems;

import java.util.ArrayList;

public class NearlyUnique extends Problem {
    public NearlyUnique(int col, ArrayList<Integer> row, String name) {
        this.Severe = false;
        this.title = name + " is nearly unique";
        this.description = "The majority of values are unique with some duplicate values";
        this.column = col;
        this.rows = row;
    }
}
