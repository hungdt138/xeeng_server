package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class RemoveFriendResponse extends AbstractResponseMessage
{

    public String mErrorMsg;
    
    public void setSuccess(int aCode)
    {
        mCode = aCode;
    }

    public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew()
    {
        return new RemoveFriendResponse();
    }
}
