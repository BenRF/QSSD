package parse.problems;

public class NearlyUnique extends Problem {
    public NearlyUnique(int col, int row) {
        this.Severe = false;
        this.title = "Column is nearly unique";
        this.description = "The majority of values are unique with some duplicate values";
        this.column = col;
        this.row = row;
    }
}
