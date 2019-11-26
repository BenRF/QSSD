package parse;

import parse.problems.MissingValues;
import parse.problems.MixedTypes;
import parse.problems.NearlyUnique;
import parse.problems.Problem;

import java.util.*;

public class ParseColumn {
    private String name;
    private int id;
    private ArrayList<Object> content;
    private boolean uniqueValues;
    private int numOfUniqueVals;
    private boolean sameType;
    private boolean empty;
    Expression format;
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

    private void performChecks() {
        this.isEmpty();
        if (!this.empty) {
            this.findExpressions();
            this.checkUnique();
            this.checkTypes();
            this.hasEmpty();
        } else {
            this.format = null;
            this.uniqueValues = false;
            this.sameType = false;
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
            this.errors.add(new MissingValues(this.id,nulls));
        }
    }

    private void checkUnique() {
        Set<Object> content = new HashSet<>(this.content);
        this.numOfUniqueVals = content.size();
        this.uniqueValues = content.size() == this.content.size();
        ArrayList<Integer> flags = new ArrayList<>();
        if (content.size() > this.content.size() * 0.85 && content.size() < this.content.size()) {
            for (int i = 0; i < this.content.size(); i++) {
                if (this.count(this.content.get(i)) > 1) {
                    flags.add(i);
                }
            }
            this.errors.add(new NearlyUnique(this.id,flags));
            System.out.println(this.name + " was nearly unique");
        }
    }

    ArrayList<Problem> getProblems() {
        return this.errors;
    }

    private int count(Object o) {
        int count = 0;
        for (Object c: this.content) {
            if (c != null) {
                if (c.equals(o)) {
                    count++;
                }
            }
        }
        return count;
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
                    if (!this.content.get(i).getClass().equals(entry.getKey())) {
                        flags.add(i);
                    }
                }
            }
        }
        if (flags.size() > 0) {
            this.errors.add(new MixedTypes(this.id,flags));
            System.out.println(this.name + " was nearly one type");
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
        this.performChecks();
        String output = "";
        if (this.empty) {
            return "EMPTY";
        } else {
            if (this.uniqueValues) {
                output = output + "Unique";
            } else {
                output = output + "!Unique";
            }
            if (this.sameType) {
                output = output + "," + this.content.get(0).getClass().getSimpleName();
            } else {
                output = output + ",Mixed type";
            }
            if (this.format.toString().length() > 0) {
                output = output + ", Format found";
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

    private void isEmpty() {
        boolean empty = true;
        for (Object o: this.content) {
            if (o != null) {
                empty = false;
            }
        }
        this.empty = empty;
    }

    private void findExpressions() {
        int i = 0;
        while(this.content.get(i) == null) {
            i++;
        }
        Expression e = new Expression(this.content.get(i));
        while (i < this.content.size()) {
            if (this.content.get(i) != null) {
                e = new Expression(e, new Expression(this.content.get(i)));
            }
            i++;
        }
        this.format = e;
    }

    boolean checkType(ParseColumn p2) {
        return this.sameType && p2.sameType && this.content.get(0).getClass().equals(p2.content.get(0).getClass());
    }

    int[] checkContent(ParseColumn p2) {
        int[] results = new int[2];
        ArrayList<Object> c1 = new ArrayList<>(this.content);
        ArrayList<Object> c2 = new ArrayList<>(p2.content);
        boolean found = false;
        int i1 = 0;
        while (i1 < c1.size()){
            for (int i2 = 0; i2 < c2.size(); i2++) {
                if (c1.get(i1).equals(c2.get(i2))) {
                    //c1.remove(i1);
                    //c2.remove(i2);
                    c1.removeAll(Collections.singletonList(c1.get(i1)));
                    c2.removeAll(Collections.singletonList(c2.get(i2)));
                    found = true;
                    break;
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
}
