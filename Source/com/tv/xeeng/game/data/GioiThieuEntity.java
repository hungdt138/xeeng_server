/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

/**
 *
 * @author tuandavrus
 */
public class GioiThieuEntity {
    private long userId;
    private String phoneOrMail;
    private int partnerId;
    
    
    public GioiThieuEntity(long userId, String phoneOrMail, int partnerId)
    {
        this.userId = userId;
        this.phoneOrMail = phoneOrMail;
        this.partnerId = partnerId;
    }

    
    /**
     * @return the userId
     */
    public long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * @return the phoneOrMail
     */
    public String getPhoneOrMail() {
        return phoneOrMail;
    }

    /**
     * @param phoneOrMail the phoneOrMail to set
     */
    public void setPhoneOrMail(String phoneOrMail) {
        this.phoneOrMail = phoneOrMail;
    }

    /**
     * @return the partnerId
     */
    public int getPartnerId() {
        return partnerId;
    }

    /**
     * @param partnerId the partnerId to set
     */
    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    
    
}
