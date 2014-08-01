package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class PrivateChatResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public long sourceID;
    public String message;
    public String username;
    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess(int aCode, long s, String message,String username_) {
        mCode = aCode;
        sourceID = s;
        this.message = message;
        username =username_;
    }
    
    public IResponseMessage createNew() {
        return new PrivateChatResponse();
    }
}
