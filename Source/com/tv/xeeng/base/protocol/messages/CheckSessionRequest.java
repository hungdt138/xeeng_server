package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class CheckSessionRequest extends AbstractRequestMessage {
    public String ip;
    public IRequestMessage createNew()
    {
        return new CheckSessionRequest();
    }
}
