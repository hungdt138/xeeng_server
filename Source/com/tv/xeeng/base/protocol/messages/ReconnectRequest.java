package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class ReconnectRequest extends AbstractRequestMessage {
    public long matchId;
    public long uid;
    public int zone;
    public int phong;
    public String username;
    public String pass;
    public int type;
    public int tourID;
    public boolean isMxh;
    public int protocol;
    public IRequestMessage createNew()
    {
        return new ReconnectRequest();
    }
}
