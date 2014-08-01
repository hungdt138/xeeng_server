/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.tienlen.data;

/**
 *
 * @author tuanda
 */
public class Duty {
    private int type;
    private static final int NO_DUTY = 0, ALL_DUTY_2X = 1, 
            A_DUTY_2X = 2, AA_DUTY_2X = 3, ABC_DUTY_2X = 4, ABCD_DUTY_2x= 5, 
            AAA_DUTY_3X = 6, ABCDE_3X = 7, ABCDEF_DUTY_3X = 8;

    private long currDutyPlayerId;
    private boolean yourDuty;
    public final static int CARDS_NULL = 0, CARDS_SINGLE = 1, CARDS_COUPLE = 2,
			CARDS_XAMCO = 3, CARDS_TUQUY = 4, CARDS_SERIAL = 5,
			CARDS_SERIAL_COUPLE = 6;
    
    private int bonusTimes;
    
    public Duty()
    {
        type = NO_DUTY;
//        Random rand = new Random();
//        type = rand.nextInt(9);
//        type = 2;
//        currDutyPlayerId =0;
//        bonusTimes = 1;
//        yourDuty = false;
    }
    
    public void checkDuty(TienLenPlayer player, int cardType, String cards)
    {
        yourDuty = false;
        
        if(type == NO_DUTY)
            return;
        
        if(type == ALL_DUTY_2X)
        {
            yourDuty = true;
            currDutyPlayerId = player.id;
            bonusTimes =2;
            return;
        }
        
        int cardLength = cards.split("#").length;
        if((type == A_DUTY_2X && cardType == CARDS_SINGLE) || (type == AA_DUTY_2X && cardType == CARDS_COUPLE) || 
            (type == ABC_DUTY_2X && CARDS_SERIAL == cardType && cardLength == 3) ||
            (type == ABCD_DUTY_2x && CARDS_SERIAL == cardType && cardLength == 4))
        {
            yourDuty = true;
            currDutyPlayerId = player.id;
            bonusTimes =2;
            return;
        }
        
        
        if((type == AAA_DUTY_3X && cardType == CARDS_XAMCO) || 
                (type == ABCDE_3X && CARDS_SERIAL == cardType && cardLength == 5)  ||
                (type == ABCDEF_DUTY_3X && CARDS_SERIAL == cardType && cardLength == 6))
        {
             yourDuty = true;
             currDutyPlayerId = player.id;
             bonusTimes =3; 
            
        }
        
        
    }
    
     @Override
    public String toString()
    {
        switch(type)
        {
            case NO_DUTY:
                return "";
            
            case ALL_DUTY_2X:
                return "(hoàn thành nhiệm vụ nhân đôi tất cả)";    
            case A_DUTY_2X:
                return "(hoàn thành nhiệm vụ A*2)";        
            case AA_DUTY_2X:
                return "(hoàn thành nhiệm vụ AA*2)";            
            case ABC_DUTY_2X:
                return "(hoàn thành nhiệm vụ ABC*2)"; 
            case ABCD_DUTY_2x:
                return "(hoàn thành nhiệm vụ ABCD*2)";       
            case AAA_DUTY_3X:
                return "(hoàn thành nhiệm vụ AAA*3)"; 
            case ABCDE_3X:
                return "(hoàn thành nhiệm vụ ABCDE*3)";     
            case ABCDEF_DUTY_3X:
                return "(hoàn thành nhiệm vụ ABCDEF*3)";     
            default:
                return "";
        }
        
    }
    /**
     * @return the yourDuty
     */
    public boolean isYourDuty() {
        return yourDuty;
    }
    
    public void setYourDuty(boolean yourDuty)
    {
        this.yourDuty = yourDuty;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @return the currDutyPlayerId
     */
    public long getCurrDutyPlayerId() {
        return currDutyPlayerId;
    }

    /**
     * @return the bonusTimes
     */
    public int getBonusTimes() {
        return bonusTimes;
    }
    
    
}
