package parse;

import parse.problems.Problem;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ParseTable extends AbstractTableModel {
    private ArrayList<ParseColumn> columns;

    public ParseTable(ArrayList<ArrayList<Object>> content) {
        this.columns = new ArrayList<>();
        for (Object header: content.get(0)) {
            this.newCol(header.toString());
        }
        for (int y = 1; y < content.size(); y++) {
            for (int x = 0; x < this.columns.size(); x++) {
                this.columns.get(x).addContent(content.get(y).get(x));
            }
        }
        this.performChecks();
    }

    public ParseTable(ParseTable p1, ParseTable p2) {
        ArrayList<ParseColumn> t1 = new ArrayList<>();
        ArrayList<ParseColumn> t2 = new ArrayList<>();
        ArrayList<Link> links = p1.getLinks(p2);
        for (Link l: links) {
            Integer[] cols = l.getColIds();
            t1.add(p1.getCol(cols[0]));
            t2.add(p2.getCol(cols[1]));
        }
        if (isUnique(t1) && !isUnique(t2)) {
            ParseTable temp = p2;
            p2 = p1;
            p1 = temp;
            links = p1.getLinks(p2);
        }
        this.columns = new ArrayList<>();
        for (ParseColumn c: p1.getColumns()) {
            this.newCol(c);
        }
        ArrayList<Integer> linkedT2Cols = new ArrayList<>();
        for (Link l: links) {
            linkedT2Cols.add(l.getColIds()[1]);
        }
        for (ParseColumn c: p2.getColumns()) {
            if (!linkedT2Cols.contains(c.getId())) {
                this.newCol(c.getName());
            }
        }
        System.out.println("Before: " + this.getRowCount());
        HashSet<Object> tab2Set = p2.getCol(links.get(0).getColIds()[1]).getContentAsSet();
        int matchOfFirst = 0;
        for (int r = 0; r < this.getRowCount(); r++) {
            ArrayList<Object> row;
            boolean match;
            for (int r2 = 0; r2 < p2.getRowCount(); r2++) {
                row = p2.getRow(r2);
                match = true;
                for (Link l : links) {
                    if (!row.get(l.getColIds()[1]).equals(this.getRow(r).get(l.getColIds()[0]))) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    int count = 0;
                    tab2Set.remove(row.get(links.get(0).getColIds()[1]));
                    for (Integer i : linkedT2Cols) {
                        row.remove(i - count);
                        count++;
                    }
                    for (int c = 0; c < row.size(); c++) {
                        this.setCell(p1.getColumnCount() + c, r, row.get(c));
                    }
                    matchOfFirst++;
                    break;
                }
            }
        }
        if (tab2Set.size() > 0) {
            for (Object o: tab2Set) {
                ArrayList<Object> rowToAdd = p2.findRowByObject(links.get(0).getColIds()[1],o);
                this.newRow();
                for (Integer i : linkedT2Cols) {
                    this.setCell(i,this.getRowCount()-1,rowToAdd.get(i));
                }
                int count = 0;
                for (Integer i : linkedT2Cols) {
                    rowToAdd.remove(i - count);
                    count++;
                }
                for (int c = 0; c < rowToAdd.size(); c++) {
                    this.setCell(p1.getColumnCount() + c, this.getRowCount()-1, rowToAdd.get(c));
                }
            }
        }
        this.performChecks();
        System.out.println("AFTER: " + this.getRowCount());
    }

    public ArrayList<Link> getLinks(ParseTable p2) {
        ArrayList<ParseColumn> p1c = this.getColumns();
        ArrayList<ParseColumn> p2c = p2.getColumns();
        ArrayList<Link> links = new ArrayList<>();
        for (ParseColumn c1: p1c) {
            for (ParseColumn c2: p2c) {
                int [] content = c1.checkContent(c2);
                boolean name = c1.getName().equals(c2.getName());
                boolean type = c1.checkType(c2);
                boolean format = c1.getFormat().equals(c2.getFormat());
                if (type && (content[0] > 0 || content[1] > 0 || name || format)) {
                    //[col1Id,col2Id,sameName,%c1ContentMatch,%c2ContentMatch,formatMatch]
                    links.add(new Link(c1.getId(),c2.getId(),name,content[0],content[1]));
                }
            }
        }
        System.out.println("LINKS FOUND: " + links.toString());
        boolean removed = false;
        int x = 0;
        while (x < links.size()) {
            Link link = links.get(x);
            for (int y = 0; y < links.size(); y++) {
                Link link2 = links.get(y);
                if (link.equal(link2)) {
                    if (link.stronger(link2)) {
                        links.remove(y);
                        removed = true;
                    } else {
                        links.remove(x);
                        removed = true;
                        break;
                    }
                }
            }
            if (removed) {
                removed = false;
                x = 0;
            } else {
                x++;
            }
        }
        System.out.println("LINKS TO BE USED: " + links.toString());
        return links;
    }

    public String[][] getContent() {
        String[][] content = new String[this.getRowCount()][this.getColumnCount()];
        for (int i = 0; i < this.getRowCount(); i++) {
            content[i] = this.getRowAsString(i);
        }
        return content;
    }

    String[] getRowAsString(int i) {
        ArrayList<Object> r = this.getRow(i);
        String[] row = new String[r.size()];
        for (int y = 0; y < r.size(); y++) {
            if (r.get(y) != null) {
                row[y] = r.get(y).toString();
            } else {
                row[y] = "";
            }
        }
        return row;
    }

    public String[][] getProblemContent() {
        HashSet<Integer> problemRowIds = new HashSet<>();
        for (Problem p: this.getProblems()) {
            problemRowIds.addAll(p.getRows());
        }
        String[][] content = new String[problemRowIds.size()][this.getColumnCount()];
        int count = 0;
        for (Integer i: problemRowIds) {
            content[count] = this.getRowAsString(i);
            count++;
        }
        return content;
    }

    public ArrayList<Problem> getProblems() {
        ArrayList<Problem> problems = new ArrayList<>();
        for (ParseColumn pC: this.columns) {
            problems.addAll(pC.getProblems());
        }
        return problems;
    }

    private void performChecks() {
        for (ParseColumn pC: this.columns) {
            pC.performChecks();
        }
    }

    boolean isUnique(ArrayList<ParseColumn> columns) {
        ArrayList<ArrayList<Object>> values = new ArrayList<>();
        for (int i = 0; i < columns.get(0).size(); i++) {
            ArrayList<Object> row = new ArrayList<>();
            for (ParseColumn p: columns) {
                row.add(p.get(i));
            }
            values.add(row);
        }
        Set<ArrayList<Object>> results = new HashSet<>(values);
        return results.size() == values.size();
    }

    ArrayList<Object> findRowByObject(int columnId, Object searchFor) {
        ParseColumn selected = this.getCol(columnId);
        int row = selected.findRowByObject(searchFor);
        return this.getRow(row);
    }

    private void newRow() {
        for (ParseColumn c: this.columns) {
            c.addContent(null);
        }
    }

    public void toConsole() {
        ArrayList<String> headers = new ArrayList<>();
        for (ParseColumn col: this.columns) {
            headers.add(col.getName());
        }
        System.out.println(headers.toString());
        for (int i = 0; i < this.getRowCount(); i++) {
            System.out.println(this.getRow(i));
        }
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

    public String[] getColumnNames() {
        String[] names = new String[this.columns.size()];
        for (int i = 0; i < this.columns.size(); i++) {
            names[i] = this.columns.get(i).getName();
        }
        return names;
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

    public boolean isProblem(int col,int row) {
        return this.columns.get(col).isProblemCell(row);
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
                for (int i = 0; i < mainCol.size() - 1; i++) {
                    Object o1 = mainCol.get(i);
                    Object o2 = mainCol.get(i + 1);
                    boolean swapping = false;
                    //detecting if a swap is required
                    if (o1 == null && o2 != null) {
                        //move all null values to the bottom
                        swapping = true;
                    } else {
                        assert o2 != null;
                        if (!o1.getClass().equals(o2.getClass())) {
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
                    }
                    //swap the items if needed
                    if (swapping) {
                        sorted = false;
                        for (ParseColumn c : this.columns) {
                            c.swap(i, i + 1);
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
        String[] formats = new String[this.columns.size()];
        for (int i = 0; i < this.columns.size(); i++) {
            attributes[i] = this.columns.get(i).getAttributes();
            formats[i] = this.columns.get(i).getFormat().toString();
        }
        String[][] result = new String[2][this.columns.size()];
        result[0] = attributes;
        result[1] = formats;
        return result;
    }

    public String toString() {
        StringBuilder output = new StringBuilder("ParseTable(");
        boolean first = true;
        for (ParseColumn pC: this.columns) {
            if (first) {
                output.append(pC.toString());
                first = false;
            } else {
                output.append(",").append(pC.toString());
            }
        }
        return output + ")";
    }

    @Override
    public String getColumnName(int col) {
        return this.columns.get(col).getName();
    }

    @Override
    public int getRowCount() {
        return this.columns.get(0).size();
    }

    @Override
    public int getColumnCount() {
        return this.columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.columns.get(columnIndex).get(rowIndex);
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
