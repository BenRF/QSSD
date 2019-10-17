package parse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class parseColumn implements Comparable<parseColumn>{
    private String name;
    private ArrayList<Object> content;
    private boolean uniqueValues;
    private int numOfUniqueVals;
    private boolean sameType;

    parseColumn(String name) {
        this.name = name;
        this.content = new ArrayList<>();
    }

    void addContent(Object c) {
        content.add(c);
        performChecks();
    }

    private void performChecks() {
        this.checkUnique();
        this.checkTypes();
    }

    private void checkUnique() {
        Set<Object> content = new HashSet<>(this.content);
        this.numOfUniqueVals = content.size();
        this.uniqueValues = content.size() == this.content.size();
    }

    private void checkTypes() {
        boolean same = true;
        for (int i = 0; i < this.content.size()-1; i++) {
            same = this.content.get(i).getClass().equals(this.content.get(i + 1).getClass());
        }
        this.sameType = same;
    }

    public String toString() {
        return this.name + "[" + this.getAttributes() + "]";
    }

    int size() {
        return this.content.size();
    }

    String getName() {return this.name; }

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

    private boolean checkAtt(parseColumn p2) {
        boolean names = this.name.equals(p2.name);
        boolean bothUnique = this.uniqueValues == p2.uniqueValues;
        boolean bothSameType = this.sameType == p2.sameType;
        return bothUnique && bothSameType && names;
    }

    private boolean checkContent(parseColumn p2) {
        Set<Object> c1 = new HashSet<>(this.content);
        Set<Object> c2 = new HashSet<>(p2.content);
        return c1.equals(c2);
    }

    // 1 = current is bigger, exact same
    // 0 = current is equal, same column different data
    // -1 = current is smaller, no correlation
    @Override
    public int compareTo(parseColumn p2) {
        parseColumn p1 = this;
        if (this.checkAtt(p2) && this.checkContent(p2)) {
            return 1;
        } else if (this.checkAtt(p2) && !this.checkContent(p2)) {
            return 0;
        } else {
            return -1;
        }
    }
}
