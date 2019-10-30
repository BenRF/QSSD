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

class Part {
    private int count;
    private boolean alphabetical;
    private boolean digit;
    private boolean space;
    private boolean symbol;
    private int min;
    private int max;
    private int startPos;
    private boolean single;

    Part(Part p) {
        this.count = p.count;
        this.alphabetical = p.alphabetical;
        this.digit = p.digit;
        this.space = p.space;
        this.symbol = p.symbol;
        this.min = p.min;
        this.max = p.max;
        this.startPos = p.startPos;
        this.single = p.single;
    }

    Part(int type, int startPos) {
        this.count = 1;
        this.min = -1;
        this.max = -1;
        this.typeCalc(type);
        this.startPos = startPos;
        this.single = true;
    }

    Part(int type, int count, int startPos) {
        this.count = count;
        this.min = -1;
        this.max = -1;
        this.startPos = startPos;
        this.typeCalc(type);
        this.single = true;
    }

    public void add(Part p) {
        if (this.single && p.single) {
            this.single = false;
            if (this.count < p.count) {
                this.min = this.count;
                this.max = p.count;
            } else {
                this.max = this.count;
                this.min = this.count;
            }
            this.count = -1;
        } else if (!this.single && p.single) {
            if (p.count < this.min) {
                this.min = p.count;
            } else if (p.count > this.max) {
                this.max = p.count;
            }
        } else if (this.single) {

        }
        if (!this.sameTypes(p)) {
            if (p.alphabetical) {
                this.alphabetical = true;
            }
            if (p.digit) {
                this.digit = true;
            }
            if (p.symbol) {
                this.symbol = true;
            }
            if (p.space) {
                this.space = true;
            }
        }
    }

    private void typeCalc(int t) {
        this.alphabetical = false;
        this.digit = false;
        this.space = false;
        this.symbol = false;
        switch (t) {
            case 76:
                this.alphabetical = true;
                break;
            case 78:
                this.digit = true;
                break;
            case 35:
                this.symbol = true;
                break;
            case 83:
                this.space = true;
                break;
        }
    }

    private boolean sameTypes(Part p) {
        return this.alphabetical == p.alphabetical && this.digit == p.digit && this.symbol == p.symbol && this.space == p.space;
    }

    //-1 = no link
    // 0 = exact same
    // 1 = same type
    int compare(Part p) {
        if (this.count == p.count && this.sameTypes(p) && this.min == p.min && this.max == p.max) {
            return 0;
        } else if (this.sameTypes(p)) {
            return 1;
        } else {
            return -1;
        }
    }

    int[] getPos() {
        return new int[]{this.startPos, this.count};
    }

    @Override
    public String toString() {
        String type = "";
        boolean first = true;
        if (this.alphabetical) {
            type = "Letter";
            first = false;
        }
        if (this.digit) {
            if (first) {
                type = "Number";
                first = false;
            } else {
                type = type + "+Number";
            }
        }
        if (this.symbol) {
            if (first) {
                type = "Symbol";
                first = false;
            } else {
                type = type + "+Symbol";
            }
        }
        if (this.space) {
            if (first) {
                type = "Space";
                first = false;
            } else {
                type = type + "+Space";
            }
        }
        if (this.count == 1) {
            return "" + type;
        } else if (this.count != -1) {
            return this.count + "" + type;
        } else {
            return "(" + this.min + "-" + this.max + ")" + type;
        }
    }
}