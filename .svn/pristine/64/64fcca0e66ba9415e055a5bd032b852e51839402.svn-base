/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.protocol.IResponseMessage;


/**
 *
 * @author tuanda
 */
public class QueueEntity {
    private ISession session;
    private IResponseMessage response;
    private boolean justLogin;
    private boolean sendMessage;
    
    public QueueEntity(ISession sesion, IResponseMessage response)
    {
        this.session = sesion;
        this.response = response;
    }
    
    public QueueEntity clone()
    {
        return new QueueEntity(session, response);
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
     * @return the response
     */
    public IResponseMessage getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(IResponseMessage response) {
        this.response = response;
    }

    /**
     * @return the justLogin
     */
    public boolean isJustLogin() {
        return justLogin;
    }

    /**
     * @param justLogin the justLogin to set
     */
    public void setJustLogin(boolean justLogin) {
        this.justLogin = justLogin;
    }

    /**
     * @return the sendMessage
     */
    public boolean isSendMessage() {
        return sendMessage;
    }

    /**
     * @param sendMessage the sendMessage to set
     */
    public void setSendMessage(boolean sendMessage) {
        this.sendMessage = sendMessage;
    }
}
