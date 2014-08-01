/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.newbacay.data;

/**
 *
 * @author tuanda
 */
public class Poker {
        private int num;
        private PokerType type;
        
        public boolean equals(Poker other) {
            return (num == other.num && type.equals(other.type));
        }
        
        public static void main1(String[] args){
            for(int i = 1; i < 53; i++) {
                int num = getNum(i);
                if(num != 10 && num != 11 && num != 12 & num != 13) {
                System.out.println("I:"+i);
                System.out.println("P:" + (new Poker(i)).toIntNew());
                System.out.println("Ps:" + (new Poker(i)).toString());
                }
            }
        }
        
        @Override
        public String toString(){
            return num + type.toString();
        }
        
        public Poker() {
        }
        
        public Poker(int n, PokerType t) {
            this.num = n;
            this.type = t;
        }
        
        public int toInt(){
            return getNum() + (PokerType.toInt(type)-1) * 13;
        }
        
        public int toIntNew(){
            return num + (type.toInt()-1) * 13;
        }
        
        public Poker(int card)
        {
            num = getNum(card);
            int iType = (card - 1) / 13 + 1;
            type = PokerType.toPokerType(iType);
        }
    
        public PokerType getType() {
			return type;
		}
        
        public int getNum() {
			return num;
		}
        
        public static int getNum(int b) {
		return (b - 1) % 13 + 1;
        }

		public static String cardToString(int card) {
			String[] s = { "tep", "bich", "ro", "co" };
			return "" + (getNum(card)) + "" + s[(card - 1) / 13];
		}
        
        public boolean isGreater(Poker other){
            
                if(PokerType.toBacayInt(this.type) > PokerType.toBacayInt(other.getType()))
                    return true;
                
                if(PokerType.toBacayInt(this.type) == PokerType.toBacayInt(other.getType()))
                    return (this.getBacayNum() > other.getBacayNum());
                
                return false;
        }
        
        public int getBacayNum()
        {
            if(num == 1)
                return 14;
            return num;
        }
        
        public int getIntToCompare()
        {
            if (getNum() == 1)
                return 10 * 4 + PokerType.toBacayInt(type); //At convert to 10
            else
                return getNum() * 4 + PokerType.toBacayInt(type);//At has converted to 14    
            
            
        }

	    /**
	     * @param num the num to set
	     */
	    public void setNum(int num) {
	        this.num = num;
	    }
    }

