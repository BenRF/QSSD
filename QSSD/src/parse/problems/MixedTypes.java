package parse.problems;

public class MixedTypes extends Problem {
    public MixedTypes(int col, int row) {
        this.Severe = false;
        this.title = "Column is nearly single type";
        this.description = "The majority of values are one type, but some are of a different type";
        this.column = col;
        this.row = row;
    }
}
