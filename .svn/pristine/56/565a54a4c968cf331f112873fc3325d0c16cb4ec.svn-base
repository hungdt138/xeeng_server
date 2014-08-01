package com.tv.xeeng.base.protocol.messages;

import java.util.List;

import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

public class EnterZoneResponse extends AbstractResponseMessage {

    public String mErrorMsg;
    public int timeout;
    public List<NRoomEntity> lstRooms;
    public String value;
    public void setFailure(int aCode, String aErrorMsg) {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public void setSuccess(int aCode) {
        mCode = aCode;
    }

    public IResponseMessage createNew() {
        return new EnterZoneResponse();
    }
}
