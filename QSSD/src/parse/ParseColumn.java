package parse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParseColumn {
    private String name;
    private int id;
    private ArrayList<Object> content;
    private boolean uniqueValues;
    private int numOfUniqueVals;
    private boolean sameType;
    private ArrayList<String> format;
    private String Setformat;

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
    }

    void addContent(Object c) {
        content.add(c);
        performChecks();
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
        this.checkUnique();
        this.checkTypes();
        this.findFormat();
    }

    private void checkUnique() {
        Set<Object> content = new HashSet<>(this.content);
        this.numOfUniqueVals = content.size();
        this.uniqueValues = content.size() == this.content.size();
    }

    private void checkTypes() {
        boolean same = true;
        for (int i = 0; i < this.content.size()-1; i++) {
            if (this.content.get(i) != null) {
                same = this.content.get(i).getClass().equals(this.content.get(i + 1).getClass());
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
        String output = "";
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
        return output;
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

    void findFormat() {
        if (this.sameType) {
            if (this.content.get(0) instanceof String) {
                if (this.isEmail()) {
                    this.Setformat = "EMAIL";
                } else {
                    this.findExpressions();
                }
            } else{
                this.findExpressions();
            }
        }
    }

    void findExpressions() {
        List<Expression> expressions = new ArrayList<>();
        for (Object o: this.content) {
            expressions.add(new Expression(o));
        }
    }

    boolean isEmail() {
        for (Object o: this.content) {
            String s = (String) o;
            int at = s.indexOf("@");
            int dot = s.indexOf(".");
            if (dot > at) {
                return false;
            }
        }
        return true;
    }

    // 10 = no link
    // 0 = exact same
    // 1 = one column is a subset of another
    // 2 = columns share some elements
    int checkContent(ParseColumn p2) {
        if (this.uniqueValues && p2.uniqueValues) {
            Set<Object> c1 = new HashSet<>(this.content);
            Set<Object> c2 = new HashSet<>(p2.content);
            Set<Object> intersection = new HashSet<>(c1);
            intersection.retainAll(c2);
            if (c1.equals(c2)) {
                return 0;
            } else if (c1.containsAll(c2) || c2.containsAll(c1)) {
                return 1;
            } else if (intersection.size() > 0) {
                return 2;
            } else {
                return 10;
            }
        } else {
            int c1Size = this.content.size();
            int c2Size = p2.size();
            ArrayList<Object> c1 = new ArrayList<>(this.content);
            ArrayList<Object> c2 = new ArrayList<>(p2.content);
            int i1 = 0;
            int i2 = 0;
            boolean pair = false;
            while (i1 < c1.size()) {
                while (i2 < c2.size()) {
                    if (c1.get(i1).equals(c2.get(i2))) {
                        c1.remove(i1);
                        c2.remove(i2);
                        pair = true;
                        break;
                    }
                    i2++;
                }
                if (pair) {
                    i1 = 0;
                    i2 = 0;
                    pair = false;
                } else {
                    i1++;
                }
            }
            if (c1.size() == 0 && c2.size() == 0) {
                return 0;
            } else if (c1.size() == 0 || c2.size() == 0) {
                return 1;
            } else if (c1.size() < c1Size || c2.size() < c2Size) {
                return 2;
            } else {
                return 10;
            }
        }
    }

}
