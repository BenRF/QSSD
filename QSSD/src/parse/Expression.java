package parse;

import java.util.ArrayList;
import java.util.List;

public class Expression {
    private ArrayList<Part> expression;
    private Object o;

    public Expression(Object o) {
        this.o = o;
        this.expression = new ArrayList<>();
        String s = o.toString();
        String basic = "";
        String symbols = "/*!@#$%^&*()\"{}_[]|\\?/<>,.";
        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetter(s.charAt(i))) {
                basic = basic + "L";
            } else if (Character.isDigit(s.charAt(i))) {
                basic = basic + "N";
            } else if (Character.isSpaceChar(s.charAt(i))) {
                basic = basic + "S";
            } else if (symbols.contains("" + s.charAt(i))) {
                basic = basic + "#";
            }
        }
        Character c = basic.charAt(0);
        int count = 1;
        for (int i = 1; i < basic.length(); i++) {
            if (basic.charAt(i) == c) {
                count++;
            } else {
                if (count > 1) {
                    this.expression.add(new Part(c, count,i-count));
                } else {
                    this.expression.add(new Part(c,i-1));
                }
                count = 1;
            }
            c = basic.charAt(i);
        }
        if (count > 1) {
            this.expression.add(new Part(c, count, basic.length()-count));
        } else {
            this.expression.add(new Part(c,basic.length()-1));
        }
    }

    public Expression(Expression e1,Expression e2) {
        this.expression = new ArrayList<>();
        this.o = e2.o;
        ArrayList<ArrayList<Integer>> links = e1.compare(e2);
        int i1 = 0;
        int i2 = 0;
        for (int i = 0; i < links.size(); i++) {
            this.expression.add(e2.getPart(links.get(i).get(1)));
        }
    }

    public ArrayList<Part> getParts() {
        return this.expression;
    }

    @Override
    public String toString() {
        String output = "";
        boolean first = true;
        for (Part p : this.expression) {
            if (first) {
                output = output + p.toString();
                first = false;
            } else {
                output = output + "," + p.toString();
            }
        }
        return output;
    }

    public String getPartString(int partNum) {
        int[] pos = this.expression.get(partNum).getPos();
        return this.o.toString().substring(pos[0],pos[0]+pos[1]);
    }

    public Part getPart(int partNum) {
        return this.expression.get(partNum);
    }

    public int getSize() {
        return this.expression.size();
    }

    //  0 = exact same
    //  1 = same pattern (size and type)
    public ArrayList<ArrayList<Integer>> compare(Expression e) {
        List<Part> similarities = new ArrayList<>();
        ArrayList<ArrayList<Integer>> links = new ArrayList<>();
        int s1,s2,strength;
        ArrayList<Integer> found = new ArrayList<>();
        for (int i1 = 0; i1 < this.getSize(); i1++) {
            s1 = i1;
            s2 = -1;
            strength = 0;
            for (int i2 = 0; i2 < e.getSize(); i2++) {
                //duplicate
                if (this.getPartString(i1).equals(e.getPartString(i2)) && !found.contains(i2)) {
                    s2 = i2;
                    strength = 2;
                    break;
                //same structure and size
                } else if (this.getPart(i1).compare(e.getPart(i2)) == 0 && strength < 1 && !found.contains(i2)) {
                    s2 = i2;
                    strength = 1;
                }
            }
            if (strength != 0) {
                ArrayList<Integer> l = new ArrayList<>();
                l.add(s1);
                l.add(s2);
                found.add(s2);
                l.add(strength);
                links.add(l);
            }
        }
        return links;
    }
}