package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class AcceptJoinRequest extends AbstractRequestMessage {
	
    public long mMatchId;
    public long uid;
    public String password;
    public boolean isAccept;
    public IRequestMessage createNew()
    {
        return new AcceptJoinRequest();
    }
}
