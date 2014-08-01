/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tuandavrus
 */
public class AuditFollowEntity implements Serializable{
    private long userId; // nguoi follow
//    private String userName;
    private Date followDate;
    private long followId; // nguoi bi follow
//    private boolean sex;
    private UserEntity user;
    
    public AuditFollowEntity()
    {
        
    }
    
    public AuditFollowEntity(long userId, long followId)
    {
        this.userId = userId;
        this.followId = followId;
        
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
     * @return the followId
     */
    public long getFollowId() {
        return followId;
    }

    /**
     * @param followId the followId to set
     */
    public void setFollowId(long followId) {
        this.followId = followId;
    }

    

    /**
     * @return the followDate
     */
    public Date getFollowDate() {
        return followDate;
    }

    /**
     * @param followDate the followDate to set
     */
    public void setFollowDate(Date followDate) {
        this.followDate = followDate;
    }

    /**
     * @return the user
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    

}
