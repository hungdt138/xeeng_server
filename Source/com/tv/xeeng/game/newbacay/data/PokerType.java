package com.tv.xeeng.game.newbacay.data;

/**
 *
 * @author tuanda
 */
public enum PokerType {
	Pic,
    Tep,
    Ro,
    Co;
	
	@Override
    public String toString() {
        switch (this) {
            case Pic:
                return " Pic";
            case Tep:
                return " Tep";
            case Co:
                return " Co";
            case Ro:
                return " Ro";
            default:
                return "";
        }
    }
    
	public boolean equal(PokerType other) {
        return (this.toInt() == other.toInt());
    }
    
	public static int toInt(PokerType pt) {
        if (pt == PokerType.Tep) {
            return 1;
        }

        if (pt == PokerType.Pic) {
            return 2;
        }

        if (pt == PokerType.Ro) {
            return 3;
        }

        if (pt == PokerType.Co) {
            return 4;
        }
        return 0;
    }

    public int toInt() {

        if (this == PokerType.Tep) {
            return 2;
        }

        if (this == PokerType.Pic) {
            return 1;
        }

        if (this == PokerType.Ro) {
            return 4;
        }

        if (this == PokerType.Co) {
            return 3;
        }
        return 0;
    }

    public static int toBacayInt(PokerType pt) {
        if (pt == PokerType.Tep) {
            return 2;
        }

        if (pt == PokerType.Pic) {
            return 1;
        }

        if (pt == PokerType.Ro) {
            return 4;
        }

        if (pt == PokerType.Co) {
            return 3;
        }
        return 0;
    }

    public static PokerType toPokerType(int iType) {
        if (iType == 1) {
            return PokerType.Pic;
        }

        if (iType == 2) {
            return PokerType.Tep;
        }

        if (iType == 3) {
            return PokerType.Co;
        }

        if (iType == 4) {
            return PokerType.Ro;
        }

        return null;
    }
}
