/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.tienlen.data;

/**
 *
 * @author tuanda
 */
public enum PokerType {

    Pic, Tep, Co, Ro;

    public int toInt() {
        switch (this) {
            case Pic:
                return 1;
            case Tep:
                return 2;
            case Ro:
                return 3;
            case Co:
                return 4;
            default:
                return 0;
        }
        /*
         if (this == PokerType.Pic) {
         return 1;
         } else if (this == PokerType.Tep) {
         return 2;
         } else if (this == PokerType.Ro) {
         return 3;
         } else if (this == PokerType.Co) {
         return 4;
         } else {
         return 0;
         }
         * */
    }
}
