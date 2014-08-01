package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class CheckSessionResponse extends AbstractResponseMessage {
	
	public String value;
	public String errMsg;
	
	
    public void setSuccess(String value)
    {
        mCode = ResponseCode.SUCCESS;
        this.value = value;
    }
    public void setFailure(String msg){
    	mCode = ResponseCode.FAILURE;
    	errMsg = msg;
    }
    public IResponseMessage createNew()
    {
        return new CheckSessionResponse();
    }
}
