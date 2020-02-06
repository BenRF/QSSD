package parse;

import javax.swing.*;

public class Link {
    private int col1,col2,col1Overlap,col2Overlap;
    private boolean sameName;
    private String col1Name,col2Name;

    Link(int col1, int col2, boolean name, int col1Overlap, int col2Overlap, String col1Name, String col2Name) {
        this.col1 = col1;
        this.col2 = col2;
        this.sameName = name;
        this.col1Overlap = col1Overlap;
        this.col2Overlap = col2Overlap;
        this.col1Name = col1Name;
        this.col2Name = col2Name;
    }

    boolean equal(Link l2) {
        return (this.col1 == l2.col1 && this.col2 != l2.col2) || (this.col1 != l2.col1 && this.col2 == l2.col2);
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
        return "[" + col1 + "," + col2 + "," + sameName + "," + col1Overlap  + "," + col2Overlap + "]";
    }

    public JLabel getLabel() {
        JLabel result = new JLabel();
        String output = col1Name + " linked to " + col2Name + " because of ";
        if (this.sameName && this.col1Overlap >= 80 && this.col2Overlap >= 80) {
            output = output + "names and content";
        } else if (this.sameName) {
            output = output + "names";
        } else if (this.col1Overlap >= 80 && this.col2Overlap >= 80) {
            output = output + "content";
        }
        result.setText(output);
        return result;
    }

    Integer[] getColIds() {
        return new Integer[] {col1,col2};
    }
}
