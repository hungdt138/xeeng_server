/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.xam.data;

/**
 *
 * @author tuanda
 */
public class Poker {
    private int num;
    private PokerType type;

    public Poker(int i) {
        num = (i - 1) % 13 + 1;
        type = intToType((i - 1) / 13 + 1);
        num = (num == 1) ? 14 : (num == 2 ? 15 : num);
        /*num = i % 13;
         num = (num==1)?14:(num==2?15:(num==0?13:num));
         type = intToType((i-1) / 13);
         * */
    }

    public int toInt() {
        int n = (num == 14) ? 1 : (num == 15 ? 2 : num);
        return n + (type.toInt() - 1) * 13;
    }

    private PokerType intToType(int i) {
        switch (i) {
            case 1:
                return PokerType.Tep;
            case 2:
                return PokerType.Pic;
            case 3:
                return PokerType.Ro;
            case 4:
                return PokerType.Co;
            default:
                return null;
        }
    }

    public Poker(int n, PokerType t) {
        this.num = n;
        this.type = t;
    }

    private boolean numEqual(int otherNum) {
        if (num == 1 || num == 14) {
            return (otherNum == 1 || otherNum == 14);
        } else if (num == 2 || num == 15) {
            return (otherNum == 2 || otherNum == 15);
        } else {
            return num == otherNum;
        }
    }

    public boolean equals(Poker other) {
        return (numEqual(other.num) && (this.type == other.type));
    }

    public boolean isDoi(Poker other) {
        return (num == other.num);
    }

    public String toString() {
        String res = "";

        if (num == 14 || num == 1) {
            res += "At";
        } else if (num == 15 || num == 2) {
            res += "2";
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
            return "co";
        } else if (this.type == PokerType.Ro) {
            return "ro";
        } else if (this.type == PokerType.Pic) {
            return "pic";
        } else if (this.type == PokerType.Tep) {
            return "tep";
        }
        return "";
    }

    public PokerType getType() {
        return type;
    }

    public int getNum() {
        return num;
    }

    public int nextCard() {
        if (num == 15) {
            return 3;
        } else {
            return num + 1;
        }
    }

    public boolean isGreater(Poker other) {
        return (this.num > other.num);
    }

    public boolean isGreater1(Poker other) {
        int thisNum = this.num == 14 ? 1 : (this.num == 15 ? 2 : this.num);
        int otherNum = other.num == 14 ? 1 : (other.num == 15 ? 2 : other.num);
        return (thisNum > otherNum);
    }

    public boolean isDen() {
        return (this.type == PokerType.Pic || this.type == PokerType.Tep);
    }

    public long lostTime() {
        return ((num == 15) || (num == 2)) ? 10 : 1;
    }
}
