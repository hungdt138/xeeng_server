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
public class LineGetOtherTableResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public String number;
    public long uid;
    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess(String n) {
        mCode = ResponseCode.SUCCESS;
        number = n;
    }

    public IResponseMessage createNew() {
        return new LineGetOtherTableResponse();
    }
}
