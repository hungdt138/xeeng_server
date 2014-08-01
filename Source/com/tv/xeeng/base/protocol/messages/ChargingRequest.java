package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class ChargingRequest extends AbstractRequestMessage {
    public int partnerId;
    public String refCode ="0";
    public IRequestMessage createNew() {
        return new ChargingRequest();
    }
}
