package parse;

class Part {
    private int count;
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
    }

    Part(int type, int count, int startPos) {
        this.count = count;
        this.min = -1;
        this.max = -1;
        this.startPos = startPos;
        this.typeCalc(type);
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
                first = false;
            } else {
                type = type + "+Spa";
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
