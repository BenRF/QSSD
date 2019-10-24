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

    public parseTable(parseTable p1, parseTable p2) {
        ArrayList<parseColumn> p1c = p1.getColumns();
        ArrayList<parseColumn> p2c = p2.getColumns();
        parseColumn strongestP1C = null;
        parseColumn strongestP2C = null;
        int strongestAtt = 100;
        int strongestCont = 100;
        for (parseColumn c1: p1c) {
            for (parseColumn c2: p2c) {
                int atts = c1.checkAtt(c2);
                int content = c1.checkContent(c2);
                if (atts < strongestAtt && content < strongestCont) {
                    strongestP1C = c1;
                    strongestP2C = c2;
                    strongestAtt = atts;
                    strongestCont = content;
                }
            }
        }
        if (strongestP1C != null && strongestP2C != null) {
            parseTable p1f = p1;
            parseTable p2f = p2;
            p1f.sortBy(strongestP1C.getName());
            p2f.sortBy(strongestP2C.getName());
        }
    }

    private ArrayList<parseColumn> getColumns() {
        return this.columns;
    }

    public boolean sortBy(String colName) {
        int primaryCol = -1;
        for (int i = 0; i < this.columns.size(); i++) {
            if (this.columns.get(i).getName().equals(colName)) {
                primaryCol = i;
            }
        }
        if (primaryCol == -1) {
            return false;
        } else {
            boolean sorted = false;
            parseColumn mainCol = this.columns.get(primaryCol);
            while (!sorted) {
                sorted = true;
                for (int i = 0; i < mainCol.size()-1; i++) {
                    Object o1 = mainCol.get(i);
                    Object o2 = mainCol.get(i+1);
                    boolean swapping = false;
                    //detecting if a swap is required
                    if (o1 == null && o2 != null) {
                        //move all null values to the bottom
                        swapping = true;
                    } else if (o1 != null && o2 == null) {
                        swapping = false;
                    } else if (!o1.getClass().equals(o2.getClass())) {
                        //objects are not of the same type, ordered alphabetically by class name
                        String c1 = o1.getClass().getSimpleName();
                        String c2 = o2.getClass().getSimpleName();
                        if (c1.compareTo(c2) > 0) {
                            swapping = true;
                        }
                    } else {
                        //Objects are of same type,
                        if (o1 instanceof String) {
                            String s1 = (String) o1;
                            String s2 = (String) o2;
                            if (s1.compareTo(s2) > 0) {
                                swapping = true;
                            }
                        } else if (o1 instanceof Integer) {
                            int i1 = (int) o1;
                            int i2 = (int) o2;
                            if (i1 > i2) {
                                swapping = true;
                            }
                        } else if (o1 instanceof Double) {
                            Double d1 = (Double) o1;
                            Double d2 = (Double) o2;
                            if (d1 > d2) {
                                swapping = true;
                            }
                        }
                    }
                    //swap the items if needed
                    if (swapping) {
                        sorted = false;
                        for (parseColumn c: this.columns) {
                            c.swap(i, i+1);
                        }
                    }
                }
            }
            return true;
        }
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
