package com.tv.xeeng.base.protocol.messages;

import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class InviteResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public long sourceID;
    public long roomID;
    public int currentZone;
    public String roomName;
    public String sourceUserName;
    public int timeout = 0;
    public int phongId = 0;
    public long minBet;
    public int level;
    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess(int aCode, long s, long rID, String rN, String sourceName, long bet,int l) {
        mCode = aCode;
        sourceID = s;
        roomID = rID;
        roomName = rN;
        sourceUserName = sourceName;
        minBet = bet;
        level=l;
    }
    
    public IResponseMessage createNew() {
        return new InviteResponse();
    }
}
