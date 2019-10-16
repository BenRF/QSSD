package parse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class parseColumn {
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
            same = this.content.get(i).getClass().equals(this.content.get(i+1).getClass());
        }
        this.sameType = same;
    }

    public String toString() {
        String output = this.name + "[";
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
        return output + "]";
    }
}
