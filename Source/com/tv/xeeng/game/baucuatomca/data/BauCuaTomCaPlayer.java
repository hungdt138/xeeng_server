/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.baucuatomca.data;


import com.tv.xeeng.game.data.SimplePlayer;

/**
 *
 * @author tuanda
 */
public class BauCuaTomCaPlayer extends SimplePlayer {
    
    private long holo;
    private long cua;
    private long tom;
    private long ca;
    private long ga;
    private long huou;
    
    private long moneyShowHand;
    private double moneyForBetDouble;
    
    public BauCuaTomCaPlayer() {
    
    }
    
    public BauCuaTomCaPlayer(long uid)
    {
        this.id = uid;
        this.isGiveUp = false;
    }
    
    public void resetBet()
    {
        this.holo = 0;
        this.cua = 0;
        this.ca = 0;
        this.tom = 0;
        this.ga = 0;
        this.huou = 0;
        showHand = false;
    }
    
    /**
     * @return the moneyForBetDouble
     */
    public double getMoneyForBetDouble() {
        return moneyForBetDouble;
    }

    /**
     * @param moneyForBetDouble the moneyForBetDouble to set
     */
    public void setMoneyForBetDouble(double moneyForBetDouble) {
        this.moneyForBetDouble = moneyForBetDouble;
    }

    /**
     * @return the bau
     */
    public long getHolo() {
        return holo;
    }

    /**
     * @param bau the bau to set
     */
    public void setHolo(long bau) {
        this.holo = bau;
    }

    /**
     * @return the cua
     */
    public long getCua() {
        return cua;
    }

    /**
     * @param cua the cua to set
     */
    public void setCua(long cua) {
        this.cua = cua;
    }

    /**
     * @return the tom
     */
    public long getTom() {
        return tom;
    }

    /**
     * @param tom the tom to set
     */
    public void setTom(long tom) {
        this.tom = tom;
    }

    /**
     * @return the ca
     */
    public long getCa() {
        return ca;
    }

    /**
     * @param ca the ca to set
     */
    public void setCa(long ca) {
        this.ca = ca;
    }

    /**
     * @return the holo
     */
    public long getGa() {
        return ga;
    }

    /**
     * @param holo the holo to set
     */
    public void setGa(long holo) {
        this.ga = holo;
    }

    /**
     * @return the huu
     */
    public long getHuou() {
        return huou;
    }

    /**
     * @param huu the huu to set
     */
    public void setHuou(long huu) {
        this.huou = huu;
    }
    
    private int containType(int[] results, int type)
    {
        int count = 0;
        for(int i = 0; i< results.length; i++)
        {
            if(results[i]== type)
                count++;
        }
        
        return count;
    }
    
    private double caculatePartBet(int[] results, int type, long betMoney)
    {
        double res = 0;
        
        if(betMoney > 0)
        {
            int count = containType(results, type);
            if(count > 0)
            {
                res = count * betMoney;
            }
            else
            {
                res = -betMoney;
            }
        }
        
        return res;
    }
    
    public void calculateBetMoney(int[] results, long firstCashBet)
    {
        if(this.isOut)
        {
            moneyForBetDouble = -firstCashBet;
            return;
        }
        
        if( holo == 0&& tom==0 && cua == 0&& ca==0 && ga == 0&& huou==0)
        {
            moneyForBetDouble = 0;
            return;
        }
        
        moneyForBetDouble = 0;
        moneyForBetDouble+= caculatePartBet(results, 0, holo);
        moneyForBetDouble+= caculatePartBet(results, 1, cua);
        moneyForBetDouble+= caculatePartBet(results, 2, tom);
        moneyForBetDouble+= caculatePartBet(results, 3, ca);
        moneyForBetDouble+= caculatePartBet(results, 4, ga);
        moneyForBetDouble+= caculatePartBet(results, 5, huou);
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
