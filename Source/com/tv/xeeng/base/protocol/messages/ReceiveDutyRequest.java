package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class ReceiveDutyRequest extends AbstractRequestMessage {
	
    public int dutyId;

    public IRequestMessage createNew()
    {
        return new ReceiveDutyRequest();
    }
}
