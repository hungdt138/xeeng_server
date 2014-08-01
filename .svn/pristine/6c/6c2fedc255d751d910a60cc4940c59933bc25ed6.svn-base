/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author ThangTD
 */
public class XEJoinEventItemsResponse extends AbstractResponseMessage {

    private String message;
    
    @Override
    public IResponseMessage createNew() {
        return new XEJoinEventItemsResponse();
    }
    
    public void setResult(int aCode, String msg) {
        mCode = aCode;
        this.message = msg;
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
}
