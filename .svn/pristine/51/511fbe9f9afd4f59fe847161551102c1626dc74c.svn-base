package com.tv.xeeng.base.protocol.messages;

import java.util.Hashtable;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class GetRoomMoneyResponse extends AbstractResponseMessage
{

    public String mErrorMsg;
    
    public Hashtable<Integer, Long> moneys;
    public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess(int aCode, Hashtable<Integer, Long> m)
    {
    	mCode = aCode;
    	moneys = m;
    }

    public IResponseMessage createNew()
    {
        return new GetRoomMoneyResponse();
    }
}
