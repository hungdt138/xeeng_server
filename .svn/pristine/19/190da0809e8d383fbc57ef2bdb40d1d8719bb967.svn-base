package com.tv.xeeng.base.protocol.messages;


import com.tv.xeeng.game.data.Couple;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class TrieuPhuHelpResponse extends AbstractResponseMessage {
    
    public String mErrorMsg;
    public String value;
    
    public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }
    public void setSuccess(int aCode)
    {
        mCode = aCode;
    }
    public IResponseMessage createNew()
    {
        return new TrieuPhuHelpResponse();
    }
}
