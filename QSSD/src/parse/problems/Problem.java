package parse.problems;

import java.util.ArrayList;

public abstract class Problem {
    boolean Severe;
    String title,description;
    int column;
    ArrayList<Integer> rows;

    public String getTitle() {
        return this.title;
    }

    public boolean isProblem(int row) {
        return this.rows.contains(row);
    }
}
