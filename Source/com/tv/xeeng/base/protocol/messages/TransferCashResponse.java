package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class TransferCashResponse extends AbstractResponseMessage
{

	public String errMessage;
	public long money;
	public long source_uid;
	public long desc_uid;
	public boolean is_source;
	public String src_name= "";
	public String dest_name = "";
	
	public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        errMessage = aErrorMsg;
    }
	
    public void setSuccess(int aCode, String msg)
    {
        mCode = aCode;
        errMessage = msg;
    }
    
    public void setSuccess(int aCode, long s, long d, long m, boolean i)
    {
        mCode = aCode;
        money = m;
        source_uid = s;
        desc_uid = d;
        is_source = i;
    }

    public IResponseMessage createNew()
    {
        return new TransferCashResponse();
    }
}
