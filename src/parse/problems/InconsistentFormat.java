package parse.problems;

import java.util.ArrayList;

public class InconsistentFormat extends Problem {
    public InconsistentFormat(int col, ArrayList<Integer> rows, String name) {
        this.Severe = false;
        this.title = name + " nearly follows format";
        this.description = "Some cells break a detected format for this column";
        this.column = col;
        this.rows = rows;
    }
}
