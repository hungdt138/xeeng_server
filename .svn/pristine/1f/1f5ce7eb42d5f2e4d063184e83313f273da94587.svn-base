package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class XERemoveSocialFriendResponse extends AbstractResponseMessage
{

    private String message;
    
    public void setSuccess(int aCode)
    {
        mCode = aCode;
    }

    public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        setMessage(aErrorMsg);
    }

    public IResponseMessage createNew()
    {
        return new XERemoveSocialFriendResponse();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
