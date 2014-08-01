package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GetImageRequest extends AbstractRequestMessage
{

    public String name;
    public IRequestMessage createNew()
    {
        return new GetImageRequest();
    }
}
