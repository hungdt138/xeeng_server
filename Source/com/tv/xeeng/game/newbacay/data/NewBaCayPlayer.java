/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.newbacay.data;


import com.tv.xeeng.game.data.SimplePlayer;

/**
 *
 * @author tuanda
 */
public class NewBaCayPlayer extends SimplePlayer{
    public Poker[] playingCards;
    public int point;
    public boolean latbai = false;
    public boolean isChiaBai = false;
    public boolean isBet = false;
    private long moneyShowHand;
    
    public NewBaCayPlayer() {
    
    }
    
    public NewBaCayPlayer(long uid)
    {
        this.id = uid;
        this.isGiveUp = false;
    }
    
    public void resetPoker()
    {
        playingCards = null;
    }
    public void setPokers(Poker[] inputPoker) {
        this.playingCards = inputPoker;
        compute();
    }

    public void compute() {
        this.point = 0;
        for (Poker p : this.playingCards) {
            this.point += p.getNum();
        }
        
        if ((this.point % 10) == 0) {
                this.point = 10;
        } else {
                this.point = this.point % 10;
        }
            
        boolean flagSap = true; // trường hợp sáp
        boolean flagDay = false; // trường hợp dây
        
        for (int i = 0; i < this.playingCards.length - 1; i++)
        {
            if (playingCards[i].getNum() != playingCards[i+1].getNum())
            {
                flagSap = false;
                break;
            }
        }

        if (flagSap)
        {
            this.point = 11;
        }
        
        if (flagDay)
        {
            this.point = 12;
        }
    }
    
    private Poker greatestPoker() {
        Poker res = this.playingCards[0];
        for(int i=1; i< this.playingCards.length; i++){
            Poker p = this.playingCards[i];
            if (p.isGreater(res)){
                    res = p;
            }
        }

        return res;

    }
    
    public boolean isWin(NewBaCayPlayer other) {
        if (this.point == other.point) {
             return this.greatestPoker().isGreater(other.greatestPoker());
        } else {
            return (this.point > other.point);
        }
    }
    public String pokersToStringNew()
    {
        StringBuilder sb = new StringBuilder();
        int lastElement = this.playingCards.length -1;
        for(int i = 0; i< this.playingCards.length; i++)
        {
            Poker p = this.playingCards[i];
            
            sb.append(p.toString());
            if(i != lastElement)
            {
                sb.append(";");
            }
        }
        
        return sb.toString();
    }
    public String pokersToString()
    {
        StringBuilder sb = new StringBuilder();
        int lastElement = this.playingCards.length -1;
        for(int i = 0; i< this.playingCards.length; i++)
        {
            Poker p = this.playingCards[i];
            
            sb.append(p.toInt());
            if(i != lastElement)
            {
                sb.append("#");
            }
        }
        
        return sb.toString();
    }

    /**
     * @return the moneyShowHand
     */
    public long getMoneyShowHand() {
        return moneyShowHand;
    }

    /**
     * @param moneyShowHand the moneyShowHand to set
     */
    public void setMoneyShowHand(long moneyShowHand) {
        this.moneyShowHand = moneyShowHand;
    }

    
 
}
