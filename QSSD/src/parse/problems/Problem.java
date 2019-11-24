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

    public String getDescription() {
        return this.description;
    }

    public String getCoords() {
        return this.column + ": " + this.rows.toString();
    }

    public boolean isSevere() {
        return Severe;
    }

}
