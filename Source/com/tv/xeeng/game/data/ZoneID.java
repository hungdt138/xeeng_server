/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

/**
 *
 * @author tuanda
 */
public class ZoneID {

    public static final int BACAY = 1;
    public static final int AILATRIEUPHU = 3;
    public static final int PHOM = 4;
    public static final int TIENLEN = 5;
    public static final int NEW_PIKA = 8;
    public static final int LIENG = 9;
    public static final int NEW_BA_CAY = 11;
    public static final int BAU_CUA_TOM_CA = 12;
    public static final int PIKACHU = 31;
    public static final int SAM = 37;
    public static final int POKER = 40;
    public static final int BINH = 99;

    public static String getZoneName(int zoneID) {
        switch (zoneID) {
            case BACAY:
                return "Ba Cây";

            case PHOM:
                return "Phỏm";

            case TIENLEN:
                return "Tiến lên miền Nam";
            
            case NEW_BA_CAY:
                return "Ba Cây";

            case BAU_CUA_TOM_CA:
                return "Bầu cua tôm cá";

            case AILATRIEUPHU:
                return "Ai là triệu phú";

            case PIKACHU:
                return "Pikachu";

            case SAM:
                return "Sâm";

            case LIENG:
                return "Liêng";
            
            case POKER:
            	return "Poker";
                
            default:
                return "Toàn cục";
        }
    }

    public static int getTimeout(int zoneID) {
        switch (zoneID) {
            case PHOM:
                return 30;
                
            case TIENLEN:
                return 30;
            
            case NEW_BA_CAY:
                return 15;

            case BAU_CUA_TOM_CA:
                return 30;

            case ZoneID.AILATRIEUPHU:
                return 30;
            
            case ZoneID.POKER:
            	return 30;
            
            case ZoneID.SAM:
                return 30;
                
            default:
                return 20;
        }
    }
}
