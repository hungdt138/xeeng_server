package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

/**
 *
 * @author Dinhpv
 */
public class AcceptJoinPlayerRequest extends AbstractRequestMessage {

    public long mMatchId;
    public long uid;
    public boolean isAccept;
    public IRequestMessage createNew()
    {
        return new AcceptJoinPlayerRequest();
    }
}
