package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class QuayCNKDResponse extends AbstractResponseMessage
{

    public String errMessage;
    public String value;	
	
    public void setFailure(String aErrorMsg)
    {
        mCode = ResponseCode.FAILURE;
        errMessage = aErrorMsg;
    }
    

    public IResponseMessage createNew()
    {
        return new QuayCNKDResponse();
    }
}
