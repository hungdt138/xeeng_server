package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class OutPhongResponse extends AbstractResponseMessage {

    public String mErrorMsg;

    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess(int aCode) {
        mCode = aCode;
    }
    
    public IResponseMessage createNew() {
        return new OutPhongResponse();
    }
}
