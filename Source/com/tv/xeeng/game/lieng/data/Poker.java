/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.game.lieng.data;

import com.tv.xeeng.game.lieng.data.*;




/**
 *
 * @author tuanda
 */
    public class Poker {
            private int num;
            private PokerType type;
            private boolean latbai;

            public Poker() {
            }
            
            public Poker(int n, PokerType t) {
                this.num = n;
                this.type = t;
            }

            public Poker(int card)
            {
                num = getNum(card);
                int iType = (card-1)/13 + 1;
                type = PokerType.toPokerType(iType);

            }

            public PokerType getType() {
                            return type;
                    }
            public int getNum() {
                            return num;
                    }

            public  int getNum(int b) {
                    return (b - 1) % 13 + 1;
            }

            public  String cardToString(int card) {
                    String[] s = { "tep", "bich", "ro", "co" };
                    return "" + (getNum(card)) + "" + s[(card - 1) / 13];
            }

            public String toString()
            {
                String[] s = { "tep", "bich", "ro", "co" };
                return s[num] + PokerType.toString(type);
            }


            public int toInt(){
                int numInt = getNum();
                if(numInt == 14) //At after convert
                {
                    numInt = 1;
                }
                return numInt + (PokerType.toInt(type)-1) * 13;
            }


            public boolean isGreater(Poker other){


                    return (this.getNum() > other.getNum());
            }


            

        /**
         * @param num the num to set
         */
        public void setNum(int num) {
            this.num = num;
        }

    /**
     * @return the latbai
     */
    public boolean isLatbai() {
        return latbai;
    }

    /**
     * @param latbai the latbai to set
     */
    public void setLatbai(boolean latbai) {
        this.latbai = latbai;
    }
        }
