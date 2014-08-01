package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class BuyItemRequest extends AbstractRequestMessage
{
    public int itemId;
    
    public IRequestMessage createNew()
    {
        return new BuyItemRequest();
    }
}
