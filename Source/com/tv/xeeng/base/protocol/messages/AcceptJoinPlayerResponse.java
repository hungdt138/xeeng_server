/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author Dinhpv
 */
public class AcceptJoinPlayerResponse extends AbstractResponseMessage {

    public String message;
    public long acceptPlayerID;
    public int available = 0; //cờ tướng: chấp

    public void setSuccess(int aCode, long playerID) {
        mCode = aCode;
        acceptPlayerID = playerID;
    }

    public void setFailure(int aCode, String msg) {
        mCode = aCode;
        message = msg;
    }

    public IResponseMessage createNew() {
        return new AcceptJoinPlayerResponse();
    }
}
