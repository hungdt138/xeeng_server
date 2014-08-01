package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class PrivateChatRequest extends AbstractRequestMessage
{

    public long sourceUid;
    public long destUid;
    public String mMessage;


    public IRequestMessage createNew()
    {
        return new PrivateChatRequest();
    }
}
