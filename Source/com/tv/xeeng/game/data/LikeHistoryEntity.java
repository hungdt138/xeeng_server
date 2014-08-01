/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import java.util.Date;

/**
 *
 * @author tuandavrus
 */
public class LikeHistoryEntity {
    private long likeHitoryId;
    private long userId;
    private String userName;
    private int systemObjectId;
    private long systemObjectRecordId;
    private Date dateCreated;
    
    public LikeHistoryEntity(long likeHistoryId, long userId, String username, int systemObjectId,
            long systemObjectRecordId, Date dateCreated)
    {
        this.likeHitoryId = likeHistoryId;
        this.userId = userId;
        this.userName = username;
        this.systemObjectId = systemObjectId;
        this.systemObjectRecordId = systemObjectRecordId;
        this.dateCreated = dateCreated;
                
    }

    /**
     * @return the likeHitoryId
     */
    public long getLikeHitoryId() {
        return likeHitoryId;
    }

    /**
     * @param likeHitoryId the likeHitoryId to set
     */
    public void setLikeHitoryId(long likeHitoryId) {
        this.likeHitoryId = likeHitoryId;
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
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
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
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
    
    
}
