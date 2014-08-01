package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class FreeTopupResponse extends AbstractResponseMessage {
    public String message;
    public int times;
    public void setSuccess(int aCode)
    {
        mCode = aCode;
    }
    public void setFailure(int aCode, String msg){
    	mCode = aCode;
    	message = msg;
    }
    public IResponseMessage createNew()
    {
        return new FreeTopupResponse();
    }
}
