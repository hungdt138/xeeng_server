package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GetFreeFriendListRequest extends AbstractRequestMessage
{

    
    public int level;
    public IRequestMessage createNew()
    {
        return new GetFreeFriendListRequest();
    }

}
