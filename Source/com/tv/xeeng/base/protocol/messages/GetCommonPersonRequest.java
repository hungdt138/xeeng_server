package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GetCommonPersonRequest extends AbstractRequestMessage
{

    public int type;
    
    public IRequestMessage createNew()
    {
        return new GetCommonPersonRequest();
    }

}
