package com.tv.xeeng.base.protocol.messages;


import com.tv.xeeng.game.room.RoomEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class FindRoomByOwnerResponse extends AbstractResponseMessage
{

    public String mErrorMsg;
    public RoomEntity mRoom;

    public void setSuccess(int aCode, RoomEntity aRoom)
    {
        mCode = aCode;
        mRoom = aRoom;
    }

    public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew()
    {
        return new FindRoomByOwnerResponse();
    }
}
