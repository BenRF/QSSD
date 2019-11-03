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
