package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class XEGetAllPrivateMessagesResponse extends AbstractResponseMessage {

    private String message;

    public IResponseMessage createNew() {
        return new XEGetAllPrivateMessagesResponse();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
