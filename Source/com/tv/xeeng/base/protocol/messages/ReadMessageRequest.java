package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class ReadMessageRequest extends AbstractRequestMessage
{

    public long messID;

    public IRequestMessage createNew()
    {
        return new ReadMessageRequest();
    }
}
