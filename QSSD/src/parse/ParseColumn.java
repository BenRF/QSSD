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

    ParseColumn(String name, int id) {
        this.name = name;
        this.id = id;
        this.content = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    ParseColumn(ParseColumn c, int i) {
        this.name = c.name;
        this.id = i;
        this.content = new ArrayList<>(c.content);
        this.uniqueValues = c.uniqueValues;
        this.numOfUniqueVals = c.numOfUniqueVals;
        this.sameType = c.sameType;
        this.format = c.format;
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

    void addContent(Object c) {
        content.add(c);
    }

    void normalise(int size) {
        while (this.content.size() < size) {
            this.content.add(null);
        }
    }

    void set(int r, Object o) {
        this.content.set(r,o);
    }

    void performChecks() {
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
        if (count >= this.content.size() * 0.1) {
            this.errors.add(new MissingValues(this.id,nulls,this.name));
        }
    }

    private void checkUnique() {
        Set<Object> content = new HashSet<>(this.content);
        this.numOfUniqueVals = content.size();
        this.uniqueValues = content.size() == this.content.size();
        if (content.size() > this.content.size() * 0.85 && content.size() < this.content.size()) {
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

    ArrayList<Problem> getProblems() {
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
        return this.name + "[" + this.getAttributes() + "]";
    }

    int size() {
        return this.content.size();
    }

    String getName() { return this.name; }

    int getId() { return this.id; }

    String getAttributes() {
        String output = "";
        boolean first = true;
        if (this.empty) {
            return "EMPTY";
        } else {
            if (this.uniqueValues) {
                output = output + "Unique";
                first = false;
            }
            if (this.sameType) {
                if (first) {
                    output = this.content.get(0).getClass().getSimpleName();
                } else {
                    output = output + "," + this.content.get(0).getClass().getSimpleName();
                }
            }
            return output;
        }
    }

    Object get(int i) {
        return this.content.get(i);
    }

    int findRowByObject(Object obj) {
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

    void swap(int a, int b) {
        Object temp = this.content.get(a);
        this.content.set(a,this.content.get(b));
        this.content.set(b,temp);
    }

    boolean isEmpty() {
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

    boolean checkType(ParseColumn p2) {
        return this.sameType & p2.sameType;
    }

    int[] checkContent(ParseColumn p2) {
        int[] results = new int[2];
        ArrayList<Object> c1 = new ArrayList<>(this.content);
        ArrayList<Object> c2 = new ArrayList<>(p2.content);
        boolean found = false;
        int i1 = 0;
        while (i1 < c1.size()){
            for (int i2 = 0; i2 < c2.size(); i2++) {
                if (c1.get(i1) != null && c2.get(i2) != null) {
                    if (c1.get(i1).equals(c2.get(i2))) {
                        c1.removeAll(Collections.singletonList(c1.get(i1)));
                        c2.removeAll(Collections.singletonList(c2.get(i2)));
                        found = true;
                        break;
                    }
                }
            }
            if (found) {
                found = false;
                i1 = 0;
            } else {
                i1++;
            }
        }
        results[0] = (int)(((double)(this.content.size()-c1.size()))/this.content.size() * 100);
        results[1] = (int)(((double)(p2.content.size()-c2.size()))/p2.content.size() * 100);
        return results;
    }

    boolean isProblemCell(int row) {
        for (Problem p: this.errors) {
            if (p.isProblem(row)) {
                return true;
            }
        }
        return false;
    }
}
