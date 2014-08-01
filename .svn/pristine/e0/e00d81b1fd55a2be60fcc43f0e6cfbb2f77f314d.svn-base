package com.tv.xeeng.base.protocol.messages;


import java.util.ArrayList;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.trieuphu.data.TrieuPhuPlayer;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;



public class TrieuPhuAnswerResponse extends AbstractResponseMessage {
    
    public String value;
    public String mErrorMsg;
    
    public void setFailure(String aErrorMsg)
    {
        mCode = ResponseCode.FAILURE;
        mErrorMsg = aErrorMsg;
    }
    
    public void setSuccess(String msg)
    {
        mCode = ResponseCode.SUCCESS;
        value = msg;
    }
    
    public IResponseMessage createNew()
    {
        return new TrieuPhuAnswerResponse();
    }
}
