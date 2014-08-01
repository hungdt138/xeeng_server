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
public class PeaceResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public long uid;

    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess(int aCode, long uid) {
        mCode = aCode;
        this.uid = uid;

    }

    public IResponseMessage createNew() {
        return new PeaceResponse();
    }
}
