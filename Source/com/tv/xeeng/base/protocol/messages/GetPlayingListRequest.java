package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class GetPlayingListRequest extends AbstractRequestMessage
{

    public int mOffset;
    public int mLength;

    public IRequestMessage createNew()
    {
        return new GetPlayingListRequest();
    }

}
