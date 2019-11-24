package parse.problems;

public class MissingValues extends Problem {
    public MissingValues(int col, int row) {
        this.Severe = false;
        this.title = "Column is missing values";
        this.description = "Some cells of the column are empty";
        this.column = col;
        this.row = row;
    }
}
