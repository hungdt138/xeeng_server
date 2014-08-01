package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class BocPhomRequest extends AbstractRequestMessage {
	public long matchID;
        public long uid=-1;
        public int zoneId; //for auto
    public IRequestMessage createNew()
    {
        return new BocPhomRequest();
    }
}
