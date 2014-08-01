package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class RemoveFriendRequest extends AbstractRequestMessage
{
	public long currID;
	public long friendID;
    public IRequestMessage createNew()
    {
        return new RemoveFriendRequest();
    }
}
