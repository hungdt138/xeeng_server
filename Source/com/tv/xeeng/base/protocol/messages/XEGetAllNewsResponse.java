package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class XEGetAllNewsResponse extends AbstractResponseMessage {

    private String serializedString;

    public void setSuccess() {

    }

    public void setSerializedString(String serializedString) {
        this.serializedString = serializedString;
    }

    public String getSerializedString() {
        return serializedString;
    }

    public void setFailure() {

    }

    @Override
    public IResponseMessage createNew() {
        return new XEGetAllNewsResponse();
    }

}
