package parse;

import org.omg.CORBA.OBJ_ADAPTER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ParseColumn {
    private String name;
    private int id;
    private ArrayList<Object> content;
    boolean uniqueValues;
    private int numOfUniqueVals;
    private boolean sameType;
    private boolean empty;
    Expression format;

    ParseColumn(String name, int id) {
        this.name = name;
        this.id = id;
        this.content = new ArrayList<>();
    }

    ParseColumn(String name, int id, int startSize) {
        this.name = name;
        this.id = id;
        this.content = new ArrayList<>();
        for (int i = 0; i < startSize; i++) {
            this.content.add(null);
        }
    }

    ParseColumn(ParseColumn c, int i) {
        this.name = c.name;
        this.id = i;
        this.content = new ArrayList<>(c.content);
        this.uniqueValues = c.uniqueValues;
        this.numOfUniqueVals = c.numOfUniqueVals;
        this.sameType = c.sameType;
        this.format = c.format;
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
        } else {
            this.format = null;
            this.uniqueValues = false;
            this.sameType = false;
        }
    }

    private void checkUnique() {
        Set<Object> content = new HashSet<>(this.content);
        this.numOfUniqueVals = content.size();
        this.uniqueValues = content.size() == this.content.size();
    }

    private void checkTypes() {
        boolean same = true;
        for (int i = 0; i < this.content.size() - 1; i++) {
            try {
                if (this.content.get(i) != null && this.content.get(i+1) != null) {
                    same = this.content.get(i).getClass().equals(this.content.get(i + 1).getClass());
                }
            } catch (NullPointerException e) {
                System.out.println(this.content.get(i));
            }
        }
        this.sameType = same;
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

    void swap(int a, int b) {
        Object temp = this.content.get(a);
        this.content.set(a,this.content.get(b));
        this.content.set(b,temp);
    }

    // 10 = no link
    // 0 = exact same
    // 1 = same type, difference in uniqueness
    // 2 = both unique, different type
    int checkAtt(ParseColumn p2) {
        boolean bothUnique = this.uniqueValues == p2.uniqueValues;
        boolean bothSameType = this.sameType == p2.sameType;
        if (bothUnique && bothSameType) {
            return 0;
        } else if (bothSameType) {
            return 1;
        } else if (bothUnique) {
            return 2;
        } else {
            return 10;
        }
    }

    int intersection(ParseColumn p2) {
        Set<Object> s1 = new HashSet<>(this.content);
        Set<Object> s2 = new HashSet<>(p2.content);
        s1.retainAll(s2);
        return s1.size();
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
                    c1.remove(i1);
                    c2.remove(i2);
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
