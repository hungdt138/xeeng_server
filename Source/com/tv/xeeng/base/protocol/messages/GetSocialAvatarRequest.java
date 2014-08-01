package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GetSocialAvatarRequest extends AbstractRequestMessage
{
    public int type;
    public long uid;
    public IRequestMessage createNew()
    {
        return new GetSocialAvatarRequest();
    }
}