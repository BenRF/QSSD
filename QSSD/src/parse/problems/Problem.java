package parse.problems;

public abstract class Problem {
    boolean Severe;
    String title,description;
    int row,column;

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCoords() {
        return this.column + "" + this.row;
    }

    public boolean isSevere() {
        return Severe;
    }

}
