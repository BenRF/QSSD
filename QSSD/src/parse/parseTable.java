package parse;

import java.util.ArrayList;

public class parseTable {
    private ArrayList<parseColumn> columns;

    public parseTable(ArrayList<ArrayList<Object>> content) {
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

    public parseTable(parseTable pT) {
        this.columns = new ArrayList<>();
        for (parseColumn pC: pT.getColumns()) {
            this.newCol(pC);
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
        if (strongestP1C != null) {
            this.columns = new ArrayList<>();
            p1.sortBy(strongestP1C.getName());
            p2.sortBy(strongestP2C.getName());
            if (strongestCont == 0) {
                for (parseColumn c: p1.getColumns()) {
                    this.newCol(c);
                }
                for(parseColumn c: p2.getColumns()) {
                    if (strongestP2C.getId() != c.getId()) {
                        this.newCol(c);
                    }
                }
            } else if (strongestCont == 1) {
                parseTable t1;
                parseTable t2;
                int i1;
                int i2;
                if (p1.rowCount() > p2.rowCount()) {
                    t1 = p1;
                    i1 = strongestP1C.getId();
                    t2 = p2;
                    i2 = strongestP2C.getId();
                } else {
                    t1 = p2;
                    i1 = strongestP2C.getId();
                    t2 = p1;
                    i2 = strongestP1C.getId();
                }
                //copy content of first table
                for (parseColumn c: t1.getColumns()) {
                    this.newCol(c);
                }
                //add extra columns to new table from second table
                for (parseColumn c: t2.getColumns()) {
                    if (i2 != c.getId()) {
                        this.newCol(c.getName());
                    }
                }
                //populate new columns with data from table 2
                for (int i = 0; i < t2.rowCount(); i++) {
                    //loop through each row of t2
                    ArrayList<Object> row = t2.getRow(i);
                    for (int j = 0; j < this.rowCount(); j++) {
                        //loop through current table rows
                        if (row.get(i2) == this.getRow(j).get(i1)) {
                            //if unique values from each row match up
                            row.remove(i2);
                            for (int o = 0; o < row.size(); o++) {
                                //loop through values to add and add them to the table
                                this.setCell(t1.colCount() + o, j, row.get(o));
                            }
                        }
                    }
                }
            } else {
                int intersection = strongestP1C.intersection(strongestP2C);
                int size = (strongestP1C.size() - intersection) + (strongestP2C.size() - intersection) + intersection;
                boolean first = true;
                //get columns from first table
                for (parseColumn c: p1.getColumns()) {
                    if (first) {
                        this.newCol(c.getName(), size);
                        first = false;
                    } else {
                        this.newCol(c.getName());
                    }
                }
                //get columns from second table (excluding common column
                for (parseColumn c: p2.getColumns()) {
                    if (c.getId() != strongestP2C.getId()) {
                        this.newCol(c.getName());
                    }
                }
                //insert all values from first table
                for (int r = 0; r < p1.rowCount(); r++) {
                    ArrayList<Object> row = p1.getRow(r);
                    for (int c = 0; c < row.size(); c++) {
                        this.setCell(c,r,row.get(c));
                    }
                }
                //add in values from second table
                for (int r = 0;r < p2.rowCount(); r++) {
                    ArrayList<Object> row = p2.getRow(r);
                    //loop through current content and find match
                    for (int tr = 0; tr < this.rowCount(); tr++) {
                        //if matching row found or no match found
                        if (this.getRow(tr).get(strongestP1C.getId()) == row.get(strongestP2C.getId())) {
                            row.remove(strongestP2C.getId());
                            for (int c = 0; c < row.size(); c++) {
                                this.setCell(p1.colCount() + c, r, row.get(c));
                            }
                            break;
                        } else if (this.getRow(tr).get(strongestP1C.getId()) == null) {
                            this.setCell(strongestP1C.getId(),tr,row.get(strongestP2C.getId()));
                            row.remove(strongestP2C.getId());
                            for (int c = 0; c < row.size(); c++) {
                                System.out.println("added " + row.get(c) + " to" + (p1.colCount() + c) + "," + tr);
                                this.setCell(p1.colCount() + c, tr, row.get(c));
                            }
                            break;
                        }
                    }
                }
                this.sortBy(strongestP1C.getName());
            }
        }
    }

    public void toConsole() {
        for (int i = 0; i < this.rowCount(); i++) {
            System.out.println(this.getRow(i));
        }
    }

    private void newCol(String name, int setSize) {
        this.columns.add(new parseColumn(name,this.columns.size(),setSize));
        this.normalise();
    }

    private void newCol(String name) {
        this.columns.add(new parseColumn(name,this.columns.size()));
        this.normalise();
    }

    private void newCol(parseColumn pC) {
        this.columns.add(new parseColumn(pC,this.columns.size()));
    }

    private ArrayList<parseColumn> getColumns() {
        return this.columns;
    }

    private parseColumn getColumn(int i) {
        return this.columns.get(i);
    }

    public ArrayList<Object> getRow(int i) {
        ArrayList<Object> r = new ArrayList<>();
        for (parseColumn pC: this.columns) {
            r.add(pC.get(i));
        }
        return r;
    }

    void normalise() {
        int max = 0;
        for (parseColumn pC: this.columns) {
            if (pC.size() > max) {
                max = pC.size();
            }
        }
        for(parseColumn pC: this.columns) {
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
