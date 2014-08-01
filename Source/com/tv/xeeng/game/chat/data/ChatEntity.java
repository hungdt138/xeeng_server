/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.chat.data;

import java.util.Date;

import com.tv.xeeng.base.session.ISession;

/**
 *
 * @author tuanda
 */
public class ChatEntity {
    private ISession session;
    private String message;
    private Date dt;
    private long userId;
    private String userName;
    
    public ChatEntity(String message, ISession session, Date dt)
    {
        this.message = message;
        this.session = session;
        this.dt = dt;
        userId = session.getUID();
        userName = session.getUserName();
    }
    /**
     * @return the session
     */
    public ISession getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(ISession session) {
        this.session = session;
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
     * @return the dt
     */
    public Date getDt() {
        return dt;
    }

    /**
     * @param dt the dt to set
     */
    public void setDt(Date dt) {
        this.dt = dt;
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
    
}
