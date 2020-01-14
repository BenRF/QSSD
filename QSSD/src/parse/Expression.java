package parse;

import java.util.ArrayList;

public class Expression {
    private ArrayList<Part> expression;
    private Object o;

    public Expression(Object o) {
        this.o = o;
        this.expression = new ArrayList<>();
        String s = o.toString();
        StringBuilder basic = new StringBuilder();
        String symbols = "/*!@#$£%^&*()\"{}_[]|\\?/<>,.";
        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetter(s.charAt(i))) {
                basic.append("L");
            } else if (Character.isDigit(s.charAt(i))) {
                basic.append("N");
            } else if (Character.isSpaceChar(s.charAt(i))) {
                basic.append("S");
            } else if (symbols.contains("" + s.charAt(i))) {
                basic.append("#");
            }
        }
        char c = basic.charAt(0);
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

    public Expression(Expression e1, Expression e2) {
        this.expression = new ArrayList<>();
        this.o = e2.o;
        ArrayList<ArrayList<Integer>> links = e1.compare(e2);
        for (ArrayList<Integer> link : links) {
            this.expression.add(e2.getPart(link.get(1)));
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        boolean first = true;
        for (Part p : this.expression) {
            if (first) {
                output.append(p.toString());
                first = false;
            } else {
                output.append(",").append(p.toString());
            }
        }
        return output.toString();
    }

    private String getPartString(int partNum) {
        int[] pos = this.expression.get(partNum).getPos();
        return this.o.toString().substring(pos[0],pos[0]+pos[1]);
    }

    private Part getPart(int partNum) {
        return this.expression.get(partNum);
    }

    private int getSize() {
        return this.expression.size();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Expression) {
            boolean same = false;
            for (Part p1: this.expression) {
                for (Part p2: ((Expression) o).expression) {
                    if (p1.equals(p2)) {
                        same = true;
                    } else {
                        return false;
                    }
                }
            }
            return same;
        } else {
            return false;
        }
    }

    //  0 = exact same
    //  1 = same pattern (size and type)
    private ArrayList<ArrayList<Integer>> compare(Expression e) {
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