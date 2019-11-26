package parse.problems;

import java.util.ArrayList;

public class MissingValues extends Problem {
    public MissingValues(int col, ArrayList<Integer> rows, String name) {
        this.Severe = false;
        this.title = name + " is missing values";
        this.description = "Some cells of the column are empty";
        this.column = col;
        this.rows = rows;
    }
}
