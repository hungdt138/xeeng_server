/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tuanda
 */
public class AlertEntity implements Serializable{
    private long alertId;
    private UserEntity userEntity;
//    private long userId;
//    private String userName;
    
    private Date createdDate;
    private int alertTypeId;
    private String message;
    private int systemObjectId;
    private long systemObjectRecordId;
    private RateEntity rateEntity;
    
    
    public AlertEntity(long alertId,UserEntity userEntity, Date createdDate, int alertTypeId, String message,
            int systemObjectId, long systemObjectRecordId)
    {
        this.alertId = alertId;
        
        this.createdDate = createdDate;
        this.alertTypeId = alertTypeId;
        this.message = message;
        this.systemObjectId = systemObjectId;
        this.systemObjectRecordId = systemObjectRecordId;
        this.userEntity = userEntity;
    }

    /**
     * @return the alertId
     */
    public long getAlertId() {
        return alertId;
    }

    /**
     * @param alertId the alertId to set
     */
    public void setAlertId(long alertId) {
        this.alertId = alertId;
    }

    

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the alertTypeId
     */
    public int getAlertTypeId() {
        return alertTypeId;
    }

    /**
     * @param alertTypeId the alertTypeId to set
     */
    public void setAlertTypeId(int alertTypeId) {
        this.alertTypeId = alertTypeId;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the systemObjectId
     */
    public int getSystemObjectId() {
        return systemObjectId;
    }

    /**
     * @param systemObjectId the systemObjectId to set
     */
    public void setSystemObjectId(int systemObjectId) {
        this.systemObjectId = systemObjectId;
    }

    /**
     * @return the systemObjectRecordId
     */
    public long getSystemObjectRecordId() {
        return systemObjectRecordId;
    }

    /**
     * @param systemObjectRecordId the systemObjectRecordId to set
     */
    public void setSystemObjectRecordId(long systemObjectRecordId) {
        this.systemObjectRecordId = systemObjectRecordId;
    }

    

    /**
     * @return the userEntity
     */
    public UserEntity getUserEntity() {
        return userEntity;
    }

    /**
     * @param userEntity the userEntity to set
     */
    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    /**
     * @return the rateEntity
     */
    public RateEntity getRateEntity() {
        return rateEntity;
    }

    /**
     * @param rateEntity the rateEntity to set
     */
    public void setRateEntity(RateEntity rateEntity) {
        this.rateEntity = rateEntity;
    }
    
}
