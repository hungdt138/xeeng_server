package com.tv.xeeng.base.protocol.messages;

import java.util.ArrayList;


import java.util.List;

import com.tv.xeeng.game.data.ChargingInfo;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class ChargingResponse extends AbstractResponseMessage {

    public String errMgs;
    public ArrayList<ChargingInfo> mess = new ArrayList<ChargingInfo>();
    public List<ChargingInfo> cardInfo = null;
    public String value = null;
    public IResponseMessage createNew() {
        return new ChargingResponse();
    }

    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        errMgs = aErrorMsg;
    }
    public void setSuccess(int aCode, ArrayList<ChargingInfo> me) {
        mCode = aCode;
        this.mess = me;
    }
}
