package com.tv.xeeng.base.protocol.messages;



import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class GetDutyDetailResponse extends AbstractResponseMessage
{

    public String mErrorMsg;
    public String value;
    
    public void setSuccess(String value)
    {
        mCode = ResponseCode.SUCCESS;
        this.value = value;
    }

    public void setFailure( String aErrorMsg)
    {
        mCode = ResponseCode.FAILURE;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew()
    {
        return new GetDutyDetailResponse();
    }
}
