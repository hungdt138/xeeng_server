/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.game.lieng.data;





/**
 *
 * @author tuanda
 */
public enum PokerType{
        Pic,
        Tep,
        Ro,
        Co;
        
        public static String toString(PokerType pt)
        {
            if(pt == PokerType.Tep)
                return "tep";
            
            if(pt == PokerType.Pic)
                return "pic";
            
            if(pt == PokerType.Ro)
                return "ro";
            
            if(pt == PokerType.Co)
                return "co";
            return "";
        }
        public static int toInt(PokerType pt)
        {
            if(pt == PokerType.Tep)
                return 1;
            
            if(pt == PokerType.Pic)
                return 2;
            
            if(pt == PokerType.Ro)
                return 3;
            
            if(pt == PokerType.Co)
                return 4;
            return 0;
        }
        
        public static PokerType toPokerType(int iType)
        {
            if(iType == 1)
               return PokerType.Tep;
            
            if(iType == 2)
               return PokerType.Pic;
            
            if(iType == 3)
               return PokerType.Ro;
            
            if(iType == 4)
               return PokerType.Co;
            
            return null;
        }
}
