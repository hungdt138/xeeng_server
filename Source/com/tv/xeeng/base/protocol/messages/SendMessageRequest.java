package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class SendMessageRequest extends AbstractRequestMessage {

    public long sUID;
    public String message;
    public long dUID;
    public String title;
    public IRequestMessage createNew() {
        return new SendMessageRequest();
    }
}
