package parse;

import java.util.ArrayList;

public class parseTable {
    private ArrayList<parseColumn> columns;

    public parseTable(ArrayList<ArrayList<Object>> content) {
        this.columns = new ArrayList<>();
        for (Object header: content.get(0)) {
            this.columns.add(new parseColumn( (String) header));
        }
        for (int y = 1; y < content.size(); y++) {
            for (int x = 0; x < this.columns.size(); x++) {
                this.columns.get(x).addContent(content.get(y).get(x));
            }
        }
    }

    public String toString() {
        String output = "ParseTable(";
        boolean first = true;
        for (parseColumn pC: this.columns) {
            if (first) {
                output = output + pC.toString();
                first = false;
            } else {
                output = output + "," + pC.toString();
            }
        }
        return output + ")";
    }
}
