/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import java.util.Date;

/**
 *
 * @author tuanda
 */
public class AlertUserEntity {
//    private long advertisingId;
    private String content;
    //private Date userId;
    private long userId;
    
    public AlertUserEntity(String content, long userId)
    {
        this.userId = userId;
        this.content = content;
        
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
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

    
    
    
    
}
