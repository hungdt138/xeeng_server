/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tv.xeeng.base.protocol.messages;

import java.util.List;

import com.tv.xeeng.base.room.NRoomEntity;
import com.tv.xeeng.protocol.AbstractResponseMessage;
import com.tv.xeeng.protocol.IResponseMessage;

/**
 *
 * @author Administrator
 */
public class GetAllRoomResponse extends AbstractResponseMessage{
    List<NRoomEntity> rooms;

    public String mErrorMsg;
    public String value;
    

    public void setSuccess(int aCode, List<NRoomEntity>   rooms)
    {
        mCode = aCode;
        this.rooms = rooms;

    }

    public void setFailure(int aCode, String aErrorMsg)
    {
        mCode = aCode;
        mErrorMsg = aErrorMsg;
    }

    public IResponseMessage createNew() {
        return new GetAllRoomResponse() {};
    }

    public List<NRoomEntity> getRooms()
    {
        return rooms;
    }

}
