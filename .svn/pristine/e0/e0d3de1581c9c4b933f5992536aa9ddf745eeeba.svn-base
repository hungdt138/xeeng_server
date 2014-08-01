package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class AddSocialFriendResponse extends AbstractResponseMessage
{

    public String mMsg;
    public String value="";
    
    public void setSuccess(int aCode)
    {
        mCode = aCode;
    }

    public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        mMsg = aErrorMsg;
    }

    public IResponseMessage createNew()
    {
        return new AddSocialFriendResponse();
    }
}
