package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GetListEventRequest extends AbstractRequestMessage
{
    
    public IRequestMessage createNew()
    {
        return new GetListEventRequest();
    }
}
