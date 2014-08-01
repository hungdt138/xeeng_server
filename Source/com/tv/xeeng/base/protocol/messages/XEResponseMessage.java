package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.protocol.AbstractResponseMessage;

public abstract class XEResponseMessage extends AbstractResponseMessage {

    private String encodedItems = null;
    private String errorMsg = null;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setEncodedItems(String encodedItems) {
        this.encodedItems = encodedItems;
    }

    public String getEncodedItems() {
        return encodedItems;
    }

    public String toString() {
        if (mCode == ResponseCode.FAILURE) {
            return getErrorMsg();
        }
        return getEncodedItems();
    }
}
