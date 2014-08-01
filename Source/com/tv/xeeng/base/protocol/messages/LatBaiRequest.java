package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class LatBaiRequest extends AbstractRequestMessage
{

    public long matchID;
    public int card;
    public int zoneId;

    public IRequestMessage createNew()
    {
        return new LatBaiRequest();
    }
}
