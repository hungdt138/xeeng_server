package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class EnterRoomRequest extends AbstractRequestMessage
{
    public int roomID;
    
    public IRequestMessage createNew()
    {
        return new EnterRoomRequest();
    }
}
