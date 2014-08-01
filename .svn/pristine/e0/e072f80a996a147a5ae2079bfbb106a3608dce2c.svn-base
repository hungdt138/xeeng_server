package com.tv.xeeng.game.phom.data;

/**
 *
 * @author tuanda
 */
public class Poker {

    public int num;
    public PokerType type;

//    public Poker() {
//    }
    public String toString() {
        String res = "";

        if (num == 1) {
            res += "A";
        } else if (num == 11) {
            res += "J";
        } else if (num == 12) {
            res += "Q";
        } else if (num == 13) {
            res += "K";
        } else {
            res += num;
        }

        res += " " + pokerTypeToString();
        return res;
    }

    private String pokerTypeToString() {
        if (this.type == PokerType.Co) {
            return "cơ";
        } else if (this.type == PokerType.Ro) {
            return "rô";
        } else if (this.type == PokerType.Pic) {
            return "bích";
        } else if (this.type == PokerType.Tep) {
            return "tép";
        }
        return "";
    }

    public Poker(int n) {
        this.num = n % 13 + 1;
        int type1 = n / 13;

//        if (type1 == 0) {
//            type = PokerType.Pic;
//        }
//        if (type1 == 1) {
//            type = PokerType.Tep;
//        }
        if (type1 == 0) {
            type = PokerType.Tep;
        }
        if (type1 == 1) {
            type = PokerType.Pic;
        }
        if (type1 == 2) {
            type = PokerType.Ro;
        }
        if (type1 == 3) {
            type = PokerType.Co;
        }
    }

    public Poker(int n, PokerType t) {
        this.num = n;
        this.type = t;
    }

    public PokerType getType() {
        return type;
    }

    public int getNum() {
        return num;
    }

    public int typeToInt() {
        if (this.type == PokerType.Pic) {
            return 2;
//            return 1;
        } else if (this.type == PokerType.Tep) {
            return 1;
//            return 2;
        } else if (this.type == PokerType.Ro) {
            return 3;
        } else if (this.type == PokerType.Co) {
            return 4;
        } else {
            return 0;
        }
    }

    public int toInt() {
        //return ((this.num - 1) * 4 + typeToInt());
        return (typeToInt() - 1) * 13 + this.num;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Poker)) {
            return false;
        }

        return isEqual((Poker)other);
    }

    public boolean isEqual(Poker other) {
        if (num == other.num && type == other.type) {
            return true;
        }
        return false;
    }

    public boolean isCa(Poker other) {
        if (this.num == other.num) {
            return true;
        }
        if (((this.num - other.num <= 2) && (this.num - other.num >= -2)) && (this.type == other.type)) {
            return true;
        }
        return false;
    }

    public boolean isCaDoc(Poker other) {
        if ((Math.abs(this.num - other.num) <= 2) && (this.type == other.type)) {
            return true;
        }
        return false;
    }

    public boolean isCaNgang(Poker other) {
        if (this.num == other.num) {
            return true;
        }
        return false;
    }

    public boolean isGreater(Poker other) {
        return this.num > other.num ? true : (this.num < other.num ? false : true);
    }
}
