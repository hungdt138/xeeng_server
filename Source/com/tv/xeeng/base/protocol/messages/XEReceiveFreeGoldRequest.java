package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class XEReceiveFreeGoldRequest extends AbstractRequestMessage {
    @Override
    public IRequestMessage createNew() {
        return new XEReceiveFreeGoldRequest();
    }
}
