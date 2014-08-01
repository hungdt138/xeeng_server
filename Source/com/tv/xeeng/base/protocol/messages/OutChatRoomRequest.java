package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class OutChatRoomRequest extends AbstractRequestMessage
{

    public int chatRoomId;
    
    public IRequestMessage createNew()
    {
        return new OutChatRoomRequest();
    }

}
