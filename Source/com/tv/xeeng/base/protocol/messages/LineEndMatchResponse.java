/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


/**
 *
 * @author tuanda
 */
public class LineEndMatchResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public long winID;
    public long currID;
    public String number;
    public String message;
    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess(long cID, long wID, String n, String m) {
        mCode = ResponseCode.SUCCESS;
        winID = wID;
        number = n;
        message = m;
        currID = cID;
    }

    public IResponseMessage createNew() {
        return new LineEndMatchResponse();
    }
}
