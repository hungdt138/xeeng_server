/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.phom.data;

/**
 *
 * @author tuanda
 */
public enum PokerType{
        Pic,
        Tep,
        Co,
        Ro;
        
        public int toInt() {	
            if (this == PokerType.Pic) {
                return 1;
            } else if (this == PokerType.Tep) {
                return 2;
            } else if (this == PokerType.Ro) {
		return 3;
            } else if (this == PokerType.Co) {
                return 4;
            }
            
//            if (this == PokerType.Co) {
//                return 4;
//            } else if (this == PokerType.Ro) {
//                return 3;
//            } else if (this == PokerType.Pic) {
//		return 1;
//            } else if (this == PokerType.Tep) {
//                return 2;
//            }
            
            return 0;
	}
}
