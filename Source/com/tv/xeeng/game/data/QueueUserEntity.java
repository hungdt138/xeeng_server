package com.tv.xeeng.game.data;

import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class QueueUserEntity {
    
    private AbstractRequestMessage request;
    private ISession session;
    private IResponseMessage response;

    public QueueUserEntity(AbstractRequestMessage request, ISession session, 
            IResponseMessage response)
    {
        this.request = request;
        this.session = session;
        this.response = response;
    }
    
    public QueueUserEntity()
    {
        
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
     * @return the request
     */
    public AbstractRequestMessage getRequest() {
        return request;
    }

    /**
     * @param request the request to set
     */
    public void setRequest(AbstractRequestMessage request) {
        this.request = request;
    }
    
}
