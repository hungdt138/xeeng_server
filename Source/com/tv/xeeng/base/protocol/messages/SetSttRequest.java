package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class SetSttRequest extends AbstractRequestMessage
{
    
    public String status;
    public IRequestMessage createNew()
    {
        return new SetSttRequest();
    }
}
