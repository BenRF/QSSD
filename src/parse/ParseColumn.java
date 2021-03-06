package parse;

import parse.problems.*;

import java.util.*;

public class ParseColumn {
    private String name;
    private int id;
    private ArrayList<Object> content;
    private boolean uniqueValues;
    private int numOfUniqueVals;
    private boolean sameType;
    private boolean empty;
    private Expression format;
    private ArrayList<Problem> errors;

    public ParseColumn(String columnName, int id) {
        this.name = columnName;
        this.id = id;
        this.content = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    ParseColumn(ParseColumn column, int id) {
        this.name = column.name;
        this.id = id;
        this.content = new ArrayList<>(column.content);
        this.uniqueValues = column.uniqueValues;
        this.numOfUniqueVals = column.numOfUniqueVals;
        this.sameType = column.sameType;
        this.format = column.format;
        this.errors = new ArrayList<>();
    }

    public ParseColumn(ParseColumn column) {
        this.name = column.name;
        this.id = column.id;
        this.content = new ArrayList<>(column.content);
        this.uniqueValues = column.uniqueValues;
        this.numOfUniqueVals = column.numOfUniqueVals;
        this.sameType = column.sameType;
        this.format = column.format;
        this.errors = new ArrayList<>();
        this.errors.addAll(column.errors);
        this.empty = column.empty;
    }

    public void addContent(Object newObj) {
        content.add(newObj);
    }

    public void normalise(int newSize) {
        while (this.content.size() < newSize) {
            this.content.add(null);
        }
    }

    public void set(int rowNum, Object newObj) {
        this.content.set(rowNum,newObj);
    }

    public void performChecks() {
        this.isEmpty();
        if (!this.empty) {
            this.findExpressions();
            this.checkUnique();
            this.checkTypes();
            this.hasEmpty();
        } else {
            this.format = null;
            this.uniqueValues = false;
            this.sameType = true;
        }
    }

    private void hasEmpty() {
        int count = 0;
        ArrayList<Integer> nulls = new ArrayList<>();
        for (int i = 0; i < this.content.size(); i++) {
            if (this.content.get(i) == null) {
                count++;
                nulls.add(i);
            }
        }
        if (count != 0) {
            this.errors.add(new MissingValues(this.id,nulls,this.name));
        }
    }

    private void checkUnique() {
        Set<Object> content = new HashSet<>(this.content);
        boolean hadNull = content.remove(null);
        int nullCount = 0;
        if (hadNull) {
            for (Object o: this.content) {
                if (o == null) {
                    nullCount++;
                }
            }
        }
        this.numOfUniqueVals = content.size();
        this.uniqueValues = content.size() == this.content.size() - nullCount;
        if (content.size() > (this.content.size()-nullCount) * 0.85 && content.size() < this.content.size() - nullCount) {
            ArrayList<Integer> flags = new ArrayList<>();
            Map<Object,ArrayList<Integer>> valsWithPos = new HashMap<>();
            for (int i = 0; i < this.content.size(); i++) {
                Object o = this.content.get(i);
                if (valsWithPos.containsKey(o)) {
                    valsWithPos.get(o).add(i);
                } else {
                    ArrayList<Integer> pos = new ArrayList<>();
                    pos.add(i);
                    valsWithPos.put(o,pos);
                }
            }
            for (ArrayList<Integer> o: valsWithPos.values()) {
                if (o.size() > 1) {
                    flags.addAll(o);
                }
            }
            this.errors.add(new NearlyUnique(this.id,flags,this.name));
        }
    }

    Expression getFormat() {
        if (this.format != null) {
            return this.format;
        } else {
            return null;
        }
    }

    public ArrayList<Problem> getProblems() {
        return this.errors;
    }

    private void checkTypes() {
        Map<Class, Integer> types = new HashMap<>();
        for (Object o: this.content) {
            if (o != null) {
                if (types.containsKey(o.getClass())) {
                    types.put(o.getClass(), types.get(o.getClass()) + 1);
                } else {
                    types.put(o.getClass(), 1);
                }
            }
        }
        int size = this.content.size();
        ArrayList<Integer> flags = new ArrayList<>();
        for (Map.Entry<Class, Integer> entry : types.entrySet()) {
            if (entry.getValue() >= size * 0.85 && entry.getValue() < size) {
                for (int i = 0; i < this.content.size(); i++) {
                    if (this.content.get(i) != null) {
                        if (!this.content.get(i).getClass().equals(entry.getKey())) {
                            flags.add(i);
                        }
                    }
                }
            }
        }
        if (flags.size() > 0) {
            this.errors.add(new MixedTypes(this.id,flags,this.name));
        }
        this.sameType = types.size() == 1;
    }

    public String toString() {
        return this.name;
    }

    public int size() {
        return this.content.size();
    }

    public String getName() { return this.name; }

    public int getId() { return this.id; }

    void setId(int newId) { this.id = newId; }

    public String getAttributes() {
        String output = "";
        boolean first = true;
        if (this.empty) {
            return "Empty";
        } else {
            if (this.uniqueValues) {
                output = output + "Unique";
                first = false;
            }
            if (this.sameType) {
                if (first) {
                    int pos = 0;
                    while (pos < this.content.size() && this.content.get(pos) == null) {
                        pos++;
                    }
                    if (pos < this.content.size()) {
                        output = this.content.get(pos).getClass().getSimpleName();
                    }
                } else {
                    if (this.content.get(0) != null) {
                        output = output + "," + this.content.get(0).getClass().getSimpleName();
                    }
                }
            }
            return output;
        }
    }

    public Object get(int rowNum) {
        return this.content.get(rowNum);
    }

    public int findRowByObject(Object obj) {
        for (int i = 0; i < this.content.size(); i++) {
            if (this.content.get(i).equals(obj)) {
                return i;
            }
        }
        return -1;
    }

    HashSet<Object> getContentAsSet() {
        return new HashSet<>(this.content);
    }

    public void swap(int rowOne, int rowTwo) {
        Object temp = this.content.get(rowOne);
        this.content.set(rowOne,this.content.get(rowTwo));
        this.content.set(rowTwo,temp);
    }

    public boolean isEmpty() {
        boolean empty = true;
        for (Object o: this.content) {
            if (o != null) {
                empty = false;
                break;
            }
        }
        this.empty = empty;
        return empty;
    }

    private void findExpressions() {
        Expression overallExpression = null;
        Expression currentExpression;
        HashMap<Expression, ArrayList<Integer>> expressions = new HashMap<>();
        for (int i = 0; i < this.content.size(); i++) {
            currentExpression = new Expression(this.content.get(i));
            if (overallExpression == null) {
                overallExpression = currentExpression;
            } else {
                overallExpression = new Expression(overallExpression,currentExpression);
            }
            if (expressions.containsKey(currentExpression)) {
                expressions.get(currentExpression).add(i);
            } else {
                ArrayList<Integer> temp = new ArrayList<>();
                temp.add(i);
                expressions.put(currentExpression,temp);
            }
        }
        boolean problem = false;
        Expression majority = null;
        for (Map.Entry<Expression,ArrayList<Integer>> e: expressions.entrySet()) {
            majority = e.getKey();
            if (e.getValue().size() >= this.content.size()*0.8 && e.getValue().size() != this.content.size()) {
                problem = true;
                break;
            }
        }
        if (problem) {
            ArrayList<Integer> problemrows = new ArrayList<>();
            for (Map.Entry<Expression,ArrayList<Integer>> e: expressions.entrySet()) {
                if (e.getKey() != majority) {
                    problemrows.addAll(e.getValue());
                }
            }
            this.errors.add(new InconsistentFormat(this.id,problemrows,this.name));
        }
        this.format = overallExpression;
    }

    public boolean checkType(ParseColumn otherColumn) {
        return this.sameType & otherColumn.sameType;
    }

    public int[] checkContent(ParseColumn otherColumn) {
        int[] results = new int[2];
        HashSet<Object> c1 = this.getContentAsSet();
        HashSet<Object> c2 = otherColumn.getContentAsSet();
        for (Object o : this.getContentAsSet()) {
            c2.removeAll(Collections.singletonList(o));
        }
        for (Object o : otherColumn.getContentAsSet()) {
            c1.removeAll(Collections.singletonList(o));
        }
        results[0] = ((this.content.size() - c1.size()) / this.content.size()) * 100;
        results[1] = Math.round((((float)otherColumn.getContentAsSet().size() - (float)c2.size()) / (float)otherColumn.getContentAsSet().size()) * 100);
        return results;
    }

    boolean isProblemCell(int rowNum) {
        for (Problem p: this.errors) {
            if (p.isProblem(rowNum)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ParseColumn) {
            ParseColumn pc = (ParseColumn) o;
            if (this.name.equals(pc.getName())) {
                for (int i = 0; i < this.size(); i++) {
                    if ((this.get(i) == null && pc.get(i) != null) || (this.get(i) != null && pc.get(i) == null)) {
                        break;
                    } else if ((this.get(i) != null || pc.get(i) != null) && !this.get(i).equals(pc.get(i))) {
                        System.out.println(this.get(i) + " vs " + pc.get(i) + " = " + this.get(i).equals(pc.get(i)));
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }
}