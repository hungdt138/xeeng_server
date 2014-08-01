package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class AnnounceRequest extends AbstractRequestMessage {

    public String message;

    public IRequestMessage createNew() {
        return new AnnounceRequest();
    }
}
