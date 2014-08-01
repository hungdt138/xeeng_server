package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GetEventDetailRequest extends AbstractRequestMessage {
    public int eventId;
    public IRequestMessage createNew()
    {
        return new GetEventDetailRequest();
    }
}
