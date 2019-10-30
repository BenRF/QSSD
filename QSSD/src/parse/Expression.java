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

    // -1 = no link
    //  0 = exact same
    //  1 = links found
    public int compare(Expression e) {
        List<Part> similarities = new ArrayList<>();
        ArrayList<ArrayList<Integer>> links = new ArrayList<>();
        int s1,s2,strength;
        for (int i1 = 0; i1 < this.getSize(); i1++) {
            s1 = i1;
            s2 = -1;
            strength = 0;
            for (int i2 = 0; i2 < e.getSize(); i2++) {
                if (this.getPartString(i1).equals(e.getPartString(i2))) {
                    s2 = i2;
                    strength = 3;
                    break;
                } else if (this.getPart(i1).compare(e.getPart(i2)) == 0 && strength < 2) {
                    s2 = i2;
                    strength = 2;
                } else if (this.getPart(i1).compare(e.getPart(i2)) == 0 && strength < 1) {
                    s2 = i2;
                    strength = 1;
                }
            }
            if (strength != 0) {
                ArrayList<Integer> l = new ArrayList<>();
                l.add(s1);
                l.add(s2);
                links.add(l);
            }
        }
        System.out.println(links);
        return 0;
    }
}

class Part {
    private int count;
    private String type;
    private int min;
    private int max;
    private int startPos;

    Part(Part p) {
        this.count = p.count;
        this.type = p.type;
        this.min = p.min;
        this.max = p.max;
        this.startPos = p.startPos;
    }

    Part(int type, int startPos) {
        this.count = 1;
        this.min = -1;
        this.max = -1;
        this.typeCalc(type);
        this.startPos = startPos;
    }

    Part(int type, int count, int startPos) {
        this.count = count;
        this.min = -1;
        this.max = -1;
        this.startPos = startPos;
        this.typeCalc(type);
    }

    Part(int min,int max,int type, int startPos) {
        this.min = min;
        this.max = max;
        this.count = -1;
        this.startPos = startPos;
        this.typeCalc(type);
    }

    private void typeCalc(int t) {
        switch (t) {
            case 76:
                this.type = "Letter";
                break;
            case 78:
                this.type = "Number";
                break;
            case 35:
                this.type = "Symbol";
                break;
            case 83:
                this.type = "Space";
                break;
        }
    }

    //-1 = no link
    // 0 = exact same
    // 1 = same type
    int compare(Part p) {
        if (this.count == p.count && this.type.equals(p.type) && this.min == p.min && this.max == p.max) {
            return 0;
        } else if (this.type.equals(p.type)) {
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
        if (this.count == 1) {
            return "" + this.type;
        } else if (this.count != -1) {
            return this.count + "" + this.type;
        } else {
            return this.min + "-" + this.max + this.type;
        }
    }
}