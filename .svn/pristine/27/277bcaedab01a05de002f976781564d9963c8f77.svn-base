package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class BuyLevelResponse extends AbstractResponseMessage {

    public String errMessage;
    public long new_cash;
    public int new_level;
    public long new_moneyForLevel;

    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        errMessage = aErrorMsg;
    }

    public void setSuccess(int aCode, long cash, int level, long moneyLevel) {
        mCode = aCode;
        new_cash = cash;
        new_level = level;
        new_moneyForLevel = moneyLevel;
    }

    public IResponseMessage createNew() {
        return new BuyLevelResponse();
    }
}
