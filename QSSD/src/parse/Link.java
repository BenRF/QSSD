package parse;

import java.awt.geom.Line2D;

public class Link {
    private int col1Overlap,col2Overlap;
    private String col1,col2;
    private boolean sameName;
    private float x1,x2;

    public Link(String col1, String col2, boolean name, int col1Overlap, int col2Overlap) {
        this.col1 = col1;
        this.col2 = col2;
        this.sameName = name;
        this.col1Overlap = col1Overlap;
        this.col2Overlap = col2Overlap;
        this.x1 = -1;
        this.x2 = -1;
    }

    boolean equal(Link l2) {
        return (this.col1.equals(l2.col1) && !this.col2.equals(l2.col2)) || (!this.col1.equals(l2.col1) && this.col2.equals(l2.col2));
    }

    boolean stronger(Link l2) {
        boolean higherSimilarity = this.col1Overlap + this.col2Overlap > l2.col1Overlap + l2.col2Overlap;
        boolean sameOrHigherSimilarity = this.col1Overlap + this.col2Overlap >= l2.col1Overlap + l2.col2Overlap;
        boolean sameNameVal = this.sameName == l2.sameName;
        boolean overThreshold = this.col1Overlap > 80 || this.col2Overlap > 80;
        boolean otherHasNameMatch = !this.sameName && l2.sameName;
        boolean thisHasNameMatch = this.sameName && !l2.sameName;
        return (sameNameVal && higherSimilarity) || (overThreshold && otherHasNameMatch) || (thisHasNameMatch && sameOrHigherSimilarity);
    }

    public String toString() {
        return "[" + this.col1 + "," + this.col2 + "," + this.sameName + "," + this.col1Overlap  + "," + this.col2Overlap + "]";
    }

    public String getFirstCol() {
        return this.col1;
    }

    public String getSecondCol() {
        return this.col2;
    }

    public Line2D getLine(float table1Pos, float table2pos) {
        this.x1 = table1Pos;
        this.x2 = table2pos;
        return new Line2D.Float(this.x1, 114, this.x2, 199);
    }

    public boolean isClicked(int x, int y) {
        float x1 = this.x1 - 30;
        float x2 = this.x2 - 30;
        if (x > Math.min(x1,x2) && x < Math.max(x1,x2)) {
            float slope = 87 / (x2 - x1);
            float b = 0 - (slope * x1);
            int result = (int) ((slope * x) + b);
            return result > y - 3 && result < y + 3;
        } else {
            return false;
        }
    }
}
