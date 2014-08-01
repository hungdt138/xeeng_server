package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class AddFriendByNameRequest extends AbstractRequestMessage
{
	public String friendName;
    public IRequestMessage createNew()
    {
        return new AddFriendByNameRequest();
    }
}
