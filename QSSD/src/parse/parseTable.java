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

    public int[] getDimensions() {
        return new int[]{this.columns.get(0).size(), this.columns.size()};
    }

    public ArrayList<parseColumn> getCols() {
        return this.columns;
    }

    public String[] getHeaderNames() {
        String[] names = new String[this.columns.size()];
        for (int i = 0; i < this.columns.size(); i++) {
            names[i] = this.columns.get(i).getName();
        }
        return names;
    }

    public String[][] getColumnAttributes() {
        String[] attributes = new String[this.columns.size()];
        for (int i = 0; i < this.columns.size(); i++) {
            attributes[i] = this.columns.get(i).getAttributes();
        }
        String[][] result = new String[1][this.columns.size()];
        result[0] = attributes;
        return result;
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
