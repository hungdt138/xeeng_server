package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.base.business.XEReceiveFreeGoldBusiness;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class XEGetRemainingFreeGoldResponse extends AbstractResponseMessage {

    private int numOfReceived;
    private String message = "";

    public IResponseMessage createNew() {
        return new XEGetRemainingFreeGoldResponse();
    }

    public int getRemainingGold() {
        return XEReceiveFreeGoldBusiness.TIMES_PER_DAY - numOfReceived;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNumOfReceived() {
        return numOfReceived;
    }

    public void setNumOfReceived(int numOfReceived) {
        this.numOfReceived = numOfReceived;
    }
}
