package parse;

import java.util.ArrayList;

public class Expression {
    private ArrayList<Part> expression;
    private Object o;

    public Expression(Object o) {
        this.o = o;
        this.expression = new ArrayList<>();
        if (o != null) {
            String s = o.toString();
            StringBuilder basic = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                if (Character.isLetter(s.charAt(i))) {
                    basic.append("L");
                } else if (Character.isDigit(s.charAt(i))) {
                    basic.append("N");
                } else if (Character.isSpaceChar(s.charAt(i))) {
                    basic.append("S");
                } else {
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
                        this.expression.add(new Part(c, count, i - count));
                    } else {
                        this.expression.add(new Part(c, i - 1));
                    }
                    count = 1;
                }
                c = basic.charAt(i);
            }
            if (count > 1) {
                this.expression.add(new Part(c, count, basic.length() - count));
            } else {
                this.expression.add(new Part(c, basic.length() - 1));
            }
        }
    }

    public Expression(Expression e1, Expression e2) {
        this.expression = new ArrayList<>();
        this.o = e2.o;
        ArrayList<ArrayList<Integer>> links = e1.compare(e2);
        int pos1 = 0;
        int pos2 = 0;
        Part temp1,temp2;
        for (ArrayList<Integer> l: links) {
            temp1 = null;
            while (pos1 < l.get(0)) {
                if (temp1 == null) {
                    temp1 = e1.getPart(pos1);
                } else {
                    temp1 = new Part(temp1,e1.getPart(pos1));
                }
                pos1++;
            }
            temp2 = null;
            while (pos2 < l.get(1)) {
                if (temp2 == null) {
                    temp2 = e2.getPart(pos2);
                } else {
                    temp2 = new Part(temp2,e2.getPart(pos2));
                }
                pos2++;
            }
            if (temp1 != null && temp2 != null) {
                this.expression.add(new Part(temp1,temp2));
            } else if (temp1 != null) {
                this.expression.add(temp1);
            } else if (temp2 != null) {
                this.expression.add(temp2);
            }
            this.expression.add(e2.getPart(l.get(1)));
            pos1++;
            pos2++;
        }
        temp1 = null;
        temp2 = null;
        while (pos1 < e1.getSize()) {
            if (temp1 == null) {
                temp1 = e1.getPart(pos1);
            } else {
                temp1 = new Part(temp1,e1.getPart(pos1));
            }
            pos1++;
        }
        while (pos2 < e2.getSize()) {
            if (temp2 == null) {
                temp2 = e2.getPart(pos2);
            } else {
                temp2 = e2.getPart(pos2);
            }
            pos2++;
        }
        if (temp1 != null && temp2 != null) {
            this.expression.add(new Part(temp1,temp2));
        } else if (temp1 != null) {
            this.expression.add(temp1);
        } else if (temp2 != null) {
            this.expression.add(temp2);
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
        return this.o.toString().substring(pos[0], pos[0] + pos[1]);
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
        int s1,s2,strength,progess = 0;
        ArrayList<Integer> found = new ArrayList<>();
        for (int i1 = 0; i1 < this.getSize(); i1++) {
            s1 = i1;
            s2 = -1;
            strength = 0;
            for (int i2 = 0; i2 < e.getSize(); i2++) {
                //check if link has already been found for part
                int linkStrength = -1;
                int linkPos = -1;
                for (int i = 0; i < links.size(); i++) {
                    ArrayList<Integer> l = links.get(i);
                    if (l.get(1) == i2) {
                        linkStrength = l.get(2);
                        linkPos = i;
                        break;
                    }
                }
                //if previous link was not perfect match
                if (linkStrength != 2 && i2 > progess) {
                    //duplicate
                    if (this.getPartString(i1).equals(e.getPartString(i2))) {
                        s2 = i2;
                        strength = 2;
                        if (linkStrength != -1) {
                            links.remove(linkPos);
                        }
                        progess = i2;
                        break;
                        //same structure and size
                    } else if (this.getPart(i1).compare(e.getPart(i2)) == 0 && strength < 1) {
                        s2 = i2;
                        strength = 1;
                    }
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
        boolean remove;
        ArrayList<Integer> removing = new ArrayList<>();
        for (int i = 0; i < links.size(); i++) {
            ArrayList<Integer> l = links.get(i);
            remove = false;
            for (int y = i; y < links.size(); y++) {
                ArrayList<Integer> l2 = links.get(y);
                if (l.get(1) > l2.get(1) && l.get(2) <= l.get(0)) {
                    remove = true;
                    break;
                }
            }
            if (remove) {
                removing.add(i);
            }
        }
        int count = 0;
        for (Integer i: removing) {
            links.remove(i-count);
            count++;
        }
        return links;
    }
}