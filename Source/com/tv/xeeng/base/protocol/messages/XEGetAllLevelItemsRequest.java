package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractRequestMessage;
import com.tv.xeeng.protocol.IRequestMessage;

public class XEGetAllLevelItemsRequest extends AbstractRequestMessage {

    private int zoneId;

    @Override
    public IRequestMessage createNew() {
        return new XEGetAllLevelItemsRequest();
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

}
