package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GetFollowRequest extends AbstractRequestMessage
{

    
    public int pageIndex;
    
    public IRequestMessage createNew()
    {
        return new GetFollowRequest();
    }

}
