package parse;

public class Part {
    private int count;
    private boolean multiPart;
    private boolean alphabetical;
    private boolean digit;
    private boolean space;
    private boolean symbol;
    private int min;
    private int max;
    private int startPos;

    Part(int type, int startPos) {
        this.count = 1;
        this.min = -1;
        this.max = -1;
        this.typeCalc(type);
        this.startPos = startPos;
        this.multiPart = false;
    }

    public Part(int type, int count, int startPos) {
        this.count = count;
        this.min = -1;
        this.max = -1;
        this.startPos = startPos;
        this.multiPart = false;
        this.typeCalc(type);
    }

    public Part(Part p1, Part p2) {
        this.multiPart = true;
        this.alphabetical = p1.alphabetical || p2.alphabetical;
        this.digit = p1.digit || p2.digit;
        this.space = p1.space || p2.space;
        this.symbol = p1.symbol || p2.symbol;
        if (!p1.multiPart && !p2.multiPart) {
            if (p1.count < p2.count) {
                this.min = p1.count;
                this.max = p2.count;
            } else {
                this.min = p2.count;
                this.max = p1.count;
            }
        } else if (p1.multiPart && p2.multiPart) {
            this.min = Math.min(p1.min, p2.min);
            this.max = Math.max(p1.max,p2.max);
        } else {
            Part multiPart,notMulti;
            if (p1.multiPart) {
                multiPart = p1;
                notMulti = p2;
            } else {
                multiPart = p2;
                notMulti = p1;
            }
            this.min = Math.min(notMulti.count, multiPart.min);
            this.max = Math.max(notMulti.count, multiPart.max);
        }
    }

    private void typeCalc(int t) {
        this.alphabetical = t == 76;
        this.digit = t == 78;
        this.space = t == 83;
        this.symbol = t == 35;
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Part) {
            Part oP = (Part) o;
            boolean alpha = this.alphabetical == oP.alphabetical;
            boolean numer = this.digit == oP.digit;
            boolean symbo = this.symbol == oP.symbol;
            boolean spac = this.space == oP.space;
            return this.count == oP.count && alpha && numer && symbo && spac;
        } else {
            return false;
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
            type = "Let";
            first = false;
        }
        if (this.digit) {
            if (first) {
                type = "Num";
                first = false;
            } else {
                type = type + "+Num";
            }
        }
        if (this.symbol) {
            if (first) {
                type = "Sym";
                first = false;
            } else {
                type = type + "+Sym";
            }
        }
        if (this.space) {
            if (first) {
                type = "Spa";
            } else {
                type = type + "+Spa";
            }
        }
        if (this.count == 1 && !this.multiPart) {
            return "" + type;
        } else if (this.count != -1 && !this.multiPart) {
            return this.count + "" + type;
        } else {
            return "(" + this.min + "-" + this.max + ")" + type;
        }
    }
}
