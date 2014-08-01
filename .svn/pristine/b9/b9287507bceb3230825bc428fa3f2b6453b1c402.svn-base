package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class XEGetPrivateMessageRequest extends AbstractRequestMessage {
    private long pmId;
    
    @Override
    public IRequestMessage createNew() {
        return new XEGetPrivateMessageRequest();
    }

    public long getPmId() {
        return pmId;
    }

    public void setPmId(long pmId) {
        this.pmId = pmId;
    }
}
