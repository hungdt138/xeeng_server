package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class AllReadyResponse extends AbstractResponseMessage {

    public void setSuccess(int aCode)
    {
        mCode = aCode;
    }

    public IResponseMessage createNew()
    {
        return new AllReadyResponse();
    }
}
