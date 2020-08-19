package parse;

import parse.problems.Problem;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.util.*;

public class ParseTable extends AbstractTableModel {
    private ArrayList<ParseColumn> columns;
    private String sortedBy;
    private String name;

    public ParseTable(ArrayList<ArrayList<Object>> content) {
        sortedBy = "";
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
        ArrayList<Link> links = p1.getLinks(p2);
        this.SetupFromTwoTables(p1,p2,links);
    }

    public ParseTable(ParseTable p1, ParseTable p2, ArrayList<Link> links) {
        this.SetupFromTwoTables(p1,p2,links);
    }

    private void SetupFromTwoTables(ParseTable p1, ParseTable p2, ArrayList<Link> links) {
        ArrayList<ParseColumn> t1 = new ArrayList<>();
        ArrayList<ParseColumn> t2 = new ArrayList<>();
        for (Link l: links) {
            Integer[] cols = {p1.getColIdFromName(l.getFirstCol()),p2.getColIdFromName(l.getSecondCol())};
            t1.add(p1.getCol(cols[0]));
            t2.add(p2.getCol(cols[1]));
        }
        if (this.isUnique(t1) && !this.isUnique(t2)) {
            ParseTable temp = p2;
            p2 = p1;
            p1 = temp;
            links = p1.getLinks(p2);
        }
        this.columns = new ArrayList<>();
        for (ParseColumn c: p1.getColumns()) {
            this.newCol(c);
        }
        ArrayList<int[]> linkedT2Cols = new ArrayList<>();
        for (Link l: links) {
            linkedT2Cols.add(new int[]{p2.getColIdFromName(l.getSecondCol()),p1.getColIdFromName(l.getFirstCol())});
        }
        for (ParseColumn c: p2.getColumns()) {
            boolean match = false;
            for (int[] l: linkedT2Cols) {
                if (l[0] == c.getId()) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                this.newCol(c.getName());
            }
        }
        HashSet<Object> tab2Set = p2.getCol(p2.getColIdFromName(links.get(0).getSecondCol())).getContentAsSet();
        Object o1,o2;
        int temp1,temp2;
        this.sortByColId(this.getColIdFromName(links.get(0).getFirstCol()));
        p2.sortByColId(p2.getColIdFromName(links.get(0).getSecondCol()));
        int upTo = 0;
        for (int r = 0; r < this.getRowCount(); r++) {
            ArrayList<Object> row;
            boolean match;
            for (int r2 = upTo; r2 < p2.getRowCount(); r2++) {
                row = p2.getRow(r2);
                match = true;
                for (Link l : links) {
                    temp1 = p1.getColIdFromName(l.getFirstCol());
                    temp2 = p2.getColIdFromName(l.getSecondCol());
                    o1 = row.get(temp2);
                    o2 = this.getRow(r).get(temp1);
                    if (o1 != null && o2 != null && !o1.equals(o2)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    upTo = r2;
                    tab2Set.remove(row.get(p2.getColIdFromName(links.get(0).getSecondCol())));
                    for (int[] i : linkedT2Cols) {
                        if (this.isCellEmpty(i[1],r)) {
                            this.setCell(i[1],r,row.get(i[0]));
                        }
                        row.set(i[0],null);
                    }
                    row.removeIf(Objects::isNull);
                    for (int c = 0; c < row.size(); c++) {
                        this.setCell(p1.getColumnCount() + c, r, row.get(c));
                    }
                    break;
                }
            }
        }
        if (tab2Set.size() > 0) {
            for (Object o: tab2Set) {
                ArrayList<Object> rowToAdd = p2.findRowByObject(p2.getColIdFromName(links.get(0).getSecondCol()),o);
                this.newRow();
                for (int[] i : linkedT2Cols) {
                    this.setCell(i[1],this.getRowCount()-1,rowToAdd.get(i[0]));
                }
                for (int[] i : linkedT2Cols) {
                    rowToAdd.set(i[0],null);
                }
                rowToAdd.removeIf(Objects::isNull);
                for (int c = 0; c < rowToAdd.size(); c++) {
                    this.setCell(p1.getColumnCount() + c, this.getRowCount()-1, rowToAdd.get(c));
                }
            }
        }
        this.sortedBy = "";
        this.performChecks();
    }

    public ParseTable(ParseTable table) {
        this.name = table.name;
        this.columns = new ArrayList<>();
        this.sortedBy = table.sortedBy;
        for (ParseColumn pC: table.columns) {
            this.columns.add(new ParseColumn(pC));
        }
    }

    public void orderCols(Enumeration<TableColumn> newOrder) {
        String[] newOrderNames = new String[this.columns.size()];
        int pos = 0;
        String temp,temp2;
        while(newOrder.hasMoreElements()) {
            temp = newOrder.nextElement().getHeaderValue().toString();
            temp2 = temp.substring(0,temp.length()-1);
            if (temp2.equals(this.sortedBy)) {
                newOrderNames[pos] = temp2;
            } else {
                newOrderNames[pos] = temp;
            }
            pos++;
        }
        ArrayList<ParseColumn> result = new ArrayList<>();
        for (String name: newOrderNames) {
            for (ParseColumn col : this.columns) {
                if (col.getName().equals(name) ) {
                    col.setId(result.size());
                    result.add(col);
                }
            }
        }
        this.columns = result;
    }

    public int getColIdFromName(String name) {
        int result = -1;
        for (ParseColumn col: this.columns) {
            if (name.equals(col.getName())) {
                result = col.getId();
                break;
            }
        }
        return result;
    }

    public void sortByColName(String name) {
        this.sortByColId(this.getColIdFromName(name));
    }

    private String getNameById(int id) {
        return this.getCol(id).getName();
    }

    private void sortByColId(int columnNumber) {
        if (this.columns.size() != 0) {
            this.sortedBy = this.getNameById(columnNumber);
            this.quickSort(columnNumber, 0, this.columns.get(columnNumber).size() - 1);
        }
    }

    private void quickSort(int columnNumber, int low, int high) {
        if (low >= high) {
            return;
        }
        Object pivot = this.getCell(columnNumber,high);
        int cnt = low;
        for (int i = low; i <= high; i++) {
            if (this.objectLessThanOrEqualTo(this.getCell(columnNumber,i),pivot)) {
                this.swapRows(cnt,i);
                cnt++;
            }
        }
        quickSort(columnNumber,low,cnt-2);
        quickSort(columnNumber,cnt,high);
    }

    private boolean objectLessThanOrEqualTo(Object o1, Object o2) {
        if (o1 instanceof String && o2 instanceof String) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.compareTo(s2) <= 0;
        } else if (o1 instanceof Integer && o2 instanceof Integer) {
            Integer i1 = (Integer) o1;
            Integer i2 = (Integer) o2;
            return i1.compareTo(i2) <= 0;
        }
        return false;
    }

    private void swapRows(int rowPos1, int rowPos2) {
        for (ParseColumn c: this.columns) {
            c.swap(rowPos1,rowPos2);
        }
    }

    public JTable getSummaryJTable() {
        JTable result = new JTable(this.getColumnAttributes(),this.getHeaderNames());
        result.setEnabled(false);
        result.addMouseListener(new TableMouseListener(this));
        return result;
    }

    public JTable getFullJTable() {
        JTable result = new JTable(this.getContent(),this.getHeaderNames());
        result.setEnabled(false);
        return result;
    }

    public Object[][] getContent() {
        Object[][] content = new Object[this.getRowCount()][this.getColumnCount()];
        for (int i = 0; i < this.getRowCount(); i++) {
            content[i] = this.getRow(i).toArray();
        }
        return content;
    }

    public JTableHeader getJTableHeader(JTable tab) {
        JTableHeader result = tab.getTableHeader();
        result.addMouseListener(new TableMouseListener(this));
        return result;
    }

    public ArrayList<Link> getLinks(ParseTable p2) {
        ArrayList<ParseColumn> p1c = this.columns;
        ArrayList<ParseColumn> p2c = p2.columns;
        ArrayList<Link> links = new ArrayList<>();
        for (ParseColumn c1 : p1c) {
            for (ParseColumn c2 : p2c) {
                boolean type = c1.checkType(c2);
                if (type || c1.isEmpty() || c2.isEmpty()) {
                    int[] content = c1.checkContent(c2);
                    boolean name = c1.getName().equals(c2.getName());
                    if (content[0] > 85 || content[1] > 85 || name) {
                        //[col1Name,col2Name,sameName,%c1ContentMatch,%c2ContentMatch,formatMatch]
                        links.add(new Link(c1.getName(), c2.getName(), name, content[0], content[1]));
                    }
                }
            }
        }
        boolean removed = false;
        int x = 0;
        if (links.size() > 1) {
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
        }
        return links;
    }

    private String[] getRowAsString(int i) {
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

    public boolean hasProblems() {
        for (ParseColumn pC: this.columns) {
            if (pC.getProblems().size() > 0) {
                return true;
            }
        }
        return false;
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

    private boolean isUnique(ArrayList<ParseColumn> columns) {
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

    private ArrayList<Object> findRowByObject(int columnId, Object searchFor) {
        ParseColumn selected = this.getCol(columnId);
        int row = selected.findRowByObject(searchFor);
        return this.getRow(row);
    }

    private void newRow() {
        for (ParseColumn c: this.columns) {
            c.addContent(null);
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

    private void setCell(int column, int row, Object o) {
        this.columns.get(column).set(row,o);
    }

    private Object getCell(int column, int row) {
        return this.columns.get(column).get(row);
    }

    private Boolean isCellEmpty(int column, int row) {
        return this.getCell(column,row) == null;
    }

    public String[] getHeaderNames() {
        String[] names = new String[this.columns.size()];
        for (int i = 0; i < this.columns.size(); i++) {
            ParseColumn col = this.columns.get(i);
            if (this.sortedBy.equals(col.getName())) {
                names[i] = col.getName() + "*";
            } else {
                names[i] = col.getName();
            }
        }
        return names;
    }

    public String[][] getColumnAttributes() {
        String[] attributes = new String[this.columns.size()];
        String[] formats = new String[this.columns.size()];
        for (int i = 0; i < this.columns.size(); i++) {
            attributes[i] = this.columns.get(i).getAttributes();
            if (!this.columns.get(i).isEmpty()) {
                formats[i] = this.columns.get(i).getFormat().toString();
            }
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
        if (col < this.columns.size()) {
            return this.columns.get(col).getName();
        } else {
            return "";
        }
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

    public void setName(char letter, int num) {
        this.name = letter + "" + num;
    }

    public JLabel getJLabel() {
        JLabel name = new JLabel();
        name.setText(this.name);
        return name;
    }

    public String getName() {
        return this.name;
    }

    public ParseColumn getColumnFromName(String colName) {
        return this.columns.get(this.getColIdFromName(colName));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ParseTable) {
            ParseTable pt = (ParseTable) o;
            if (pt.getColumnCount() == this.getColumnCount()) {
                for (int i = 0; i < this.getColumnCount(); i++) {
                    if (!this.getCol(i).equals(pt.getCol(i))){
                        System.out.println(this.getCol(i).getName() + " vs " + pt.getCol(i).getName() + " = " + this.getCol(i).equals(pt.getCol(i)));
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}