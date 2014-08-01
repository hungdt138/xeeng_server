package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class SendChatRoomRequest extends AbstractRequestMessage
{

    
    public String content;
    
    public IRequestMessage createNew()
    {
        return new SendChatRoomRequest();
    }

}
