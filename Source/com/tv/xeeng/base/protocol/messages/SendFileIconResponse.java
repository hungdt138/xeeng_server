package com.tv.xeeng.base.protocol.messages;



import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class SendFileIconResponse extends AbstractResponseMessage
{

    public String mErrorMsg;
    public String value;
    public boolean isSendAlbumDetail;
    
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
        return new SendFileIconResponse();
    }
}
