/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.game.newbacay.data;


/**
 *
 * @author tuanda
 */
public class Transform {

    public static Poker numberToPoker(int input){

        switch (input){
            case 1:
                return new Poker(1, PokerType.Pic);
            case 2:
                return new Poker(1,PokerType.Tep);
            case 3:
                return new Poker(1,PokerType.Co);
            case 4:
                return new Poker(1,PokerType.Ro);
            case 5:
                return new Poker(2,PokerType.Pic);
            case 6:
                return new Poker(2,PokerType.Tep);
            case 7:
                return new Poker(2,PokerType.Co);
            case 8:
                return new Poker(2,PokerType.Ro);
            case 9:
                return new Poker(3,PokerType.Pic);
            case 10:
                return new Poker(3,PokerType.Tep);
            case 11:
                return new Poker(3,PokerType.Co);
            case 12:
                return new Poker(3,PokerType.Ro);
            case 13:
                return new Poker(4,PokerType.Pic);
            case 14:
                return new Poker(4,PokerType.Tep);
            case 15:
                return new Poker(4,PokerType.Co);
            case 16:
                return new Poker(4,PokerType.Ro);
            case 17:
                return new Poker(5,PokerType.Pic);
            case 18:
                return new Poker(5,PokerType.Tep);
            case 19:
                return new Poker(5,PokerType.Co);
            case 20:
                return new Poker(5,PokerType.Ro);
            case 21:
                return new Poker(6,PokerType.Pic);
            case 22:
                return new Poker(6,PokerType.Tep);
            case 23:
                return new Poker(6,PokerType.Co);
            case 24:
                return new Poker(6,PokerType.Ro);
            case 25:
                return new Poker(7,PokerType.Pic);
            case 26:
                return new Poker(7,PokerType.Tep);
            case 27:
                return new Poker(7,PokerType.Co);
            case 28:
                return new Poker(7,PokerType.Ro);
            case 29:
                return new Poker(8,PokerType.Pic);
            case 30:
                return new Poker(8,PokerType.Tep);
            case 31:
                return new Poker(8,PokerType.Co);
            case 32:
                return new Poker(8,PokerType.Ro);
            case 33:
                return new Poker(9,PokerType.Pic);
            case 34:
                return new Poker(9,PokerType.Tep);
            case 35:
                return new Poker(9,PokerType.Co);
            case 36:
                return new Poker(9,PokerType.Ro);
            default:
                return new Poker(0, PokerType.Pic);
        }
    }
    
    
}
