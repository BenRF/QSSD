package parse;

import java.util.ArrayList;

public class ParseTable {
    private ArrayList<ParseColumn> columns;

    public ParseTable(ArrayList<ArrayList<Object>> content) {
        this.columns = new ArrayList<>();
        for (Object header: content.get(0)) {
            this.newCol((String) header);
        }
        for (int y = 1; y < content.size(); y++) {
            for (int x = 0; x < this.columns.size(); x++) {
                this.columns.get(x).addContent(content.get(y).get(x));
            }
        }
    }

    public ParseTable(ParseTable pT) {
        this.columns = new ArrayList<>();
        for (ParseColumn pC: pT.getColumns()) {
            this.newCol(pC);
        }
    }

    public ParseTable(ParseTable p1, ParseTable p2) {
        ArrayList<ParseColumn> p1c = p1.getColumns();
        ArrayList<ParseColumn> p2c = p2.getColumns();
        ArrayList<ArrayList<Integer>> links = new ArrayList<>();
        int i1,i2,strength,content = 0;
        for (ParseColumn c1: p1c) {
            i1 = c1.getId();
            i2 = -1;
            strength = 0;
            for (ParseColumn c2: p2c) {
                content = c1.checkContent(c2);
                if (content != 10) {
                    i2 = c2.getId();
                    strength = 6;
                    break;
                } else if (c1.getName().equals(c2.getName()) && c1.sameTypes(c2)) {
                    i2 = c2.getId();
                    strength = 5;
                } else if (c1.checkType(c2) && strength < 5) {
                    i2 = c2.getId();
                    strength = 4;
                }
            }
            if (strength >= 4) {
                ArrayList<Integer> link = new ArrayList<>();
                link.add(i1);
                link.add(i2);
                link.add(strength);
                link.add(content);
                links.add(link);
            }
        }
        System.out.println(links);
        this.columns = new ArrayList<>();
        int strongestP1C = -1;
        int strongestP2C = -1;
        strength = 0;
        boolean merging = true;
        for (ArrayList<Integer> link: links) {
            if (link.get(2) > strength) {
                strongestP1C = link.get(0);
                strongestP2C = link.get(1);
                strength = link.get(2);
                if (strength != 6) {
                    merging = false;
                }
            }
        }
        p1.sortBy(p1.getCol(strongestP1C).getName());
        p2.sortBy(p2.getCol(strongestP2C).getName());
        for (ParseColumn c: p1.getColumns()) {
            this.newCol(c);
        }
        if (merging) {
            ArrayList<Integer> linkedT2Cols = new ArrayList<>();
            for (ArrayList<Integer> l: links) {
                linkedT2Cols.add(l.get(1));
            }
            for (ParseColumn c: p2.getColumns()) {
                if (!linkedT2Cols.contains(c.getId())) {
                    this.newCol(c.getName());
                }
            }
            for (int r = 0; r < p2.rowCount(); r++) {
                ArrayList<Object> row = p2.getRow(r);
                boolean match = true;
                for (int tr = 0; tr < this.rowCount(); tr++) {
                    match = true;
                    for (ArrayList<Integer> l: links) {
                        if (row.get(l.get(1)) != this.getRow(tr).get(l.get(0))) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        int count = 0;
                        for (Integer i: linkedT2Cols) {
                            row.remove(i-count);
                            count++;
                        }
                        for (int c = 0; c < row.size(); c++) {
                            this.setCell(p1.colCount() + c, r, row.get(c));
                        }
                        break;
                    }
                }
                if (!match) {
                    this.newRow();
                    int rowPos = this.rowCount()-1;
                    int count = 0;
                    for (ArrayList<Integer> link: links) {
                        this.setCell(link.get(0),rowPos,row.get(link.get(1)));
                        row.remove(link.get(1)-count);
                        count++;
                    }
                    for (int c = 0; c < row.size(); c++) {
                        this.setCell(p1.colCount() + c, rowPos, row.get(c));
                    }
                }
            }
        }
        this.sortBy(p1.getCol(strongestP1C).getName());
    }

    private void newRow() {
        for (ParseColumn c: this.columns) {
            c.addContent(null);
        }
    }

    public void toConsole() {
        for (int i = 0; i < this.rowCount(); i++) {
            System.out.println(this.getRow(i));
        }
    }

    private void newCol(String name, int setSize) {
        this.columns.add(new ParseColumn(name,this.columns.size(),setSize));
        this.normalise();
    }

    private void newCol(String name) {
        this.columns.add(new ParseColumn(name,this.columns.size()));
        this.normalise();
    }

    private void newCol(ParseColumn pC) {
        this.columns.add(new ParseColumn(pC,this.columns.size()));
    }

    private ParseColumn getCol(int i) {
        return this.columns.get(i);
    }

    private ArrayList<ParseColumn> getColumns() {
        return this.columns;
    }

    public ArrayList<Object> getRow(int i) {
        ArrayList<Object> r = new ArrayList<>();
        for (ParseColumn pC: this.columns) {
            r.add(pC.get(i));
        }
        return r;
    }

    private void normalise() {
        int max = 0;
        for (ParseColumn pC: this.columns) {
            if (pC.size() > max) {
                max = pC.size();
            }
        }
        for(ParseColumn pC: this.columns) {
            pC.normalise(max);
        }
    }

    public int rowCount() {
        try {
            return this.columns.get(0).size();
        } catch (Exception e) {
            return 0;
        }
    }

    private int colCount() {
        try {
        return this.columns.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private void sortBy(String colName) {
        int primaryCol = -1;
        for (int i = 0; i < this.columns.size(); i++) {
            if (this.columns.get(i).getName().equals(colName)) {
                primaryCol = i;
            }
        }
        if (primaryCol != -1) {
            boolean sorted = false;
            ParseColumn mainCol = this.columns.get(primaryCol);
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
                        for (ParseColumn c: this.columns) {
                            c.swap(i, i+1);
                        }
                    }
                }
            }
        }
    }

    private void setCell(int c, int r, Object o) {
        this.columns.get(c).set(r,o);
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
        for (ParseColumn pC: this.columns) {
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
