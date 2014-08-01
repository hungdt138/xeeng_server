package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class SetSocialAvatarRequest extends AbstractRequestMessage
{
    public int type;
    public long fileId;
    public IRequestMessage createNew()
    {
        return new SetSocialAvatarRequest();
    }
}
