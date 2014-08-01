package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GetItemRequest extends AbstractRequestMessage {
    public int cacheVersion;
    public IRequestMessage createNew()
    {
        return new GetItemRequest();
    }
}
