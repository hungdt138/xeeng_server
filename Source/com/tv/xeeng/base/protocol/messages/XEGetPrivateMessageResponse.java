package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class XEGetPrivateMessageResponse extends AbstractResponseMessage {

    private String message;

    public IResponseMessage createNew() {
        return new XEGetPrivateMessageResponse();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
