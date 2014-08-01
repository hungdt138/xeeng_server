package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class FreeTopupRequest extends AbstractRequestMessage {
	
    

    public IRequestMessage createNew()
    {
        return new FreeTopupRequest();
    }
}
