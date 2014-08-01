package com.tv.xeeng.base.protocol.messages.json;




import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.protocol.messages.GetPlayingListRequest;
import com.tv.xeeng.base.protocol.messages.GetPlayingListResponse;
import com.tv.xeeng.game.data.ResponseCode;
import com.tv.xeeng.game.room.RoomEntity;
import com.tv.xeeng.protocol.IMessageProtocol;
import com.tv.xeeng.protocol.IRequestMessage;
import com.tv.xeeng.protocol.IResponseMessage;


public class GetPlayingListJSON implements IMessageProtocol
{

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(GetPlayingListJSON.class);

    public boolean decode(Object aEncodedObj, IRequestMessage aDecodingObj) throws ServerException
    {
        try
        {
            // request data
            JSONObject jsonData = (JSONObject) aEncodedObj;
            // request messsage
            GetPlayingListRequest getPlayingList = (GetPlayingListRequest) aDecodingObj;
            // parsing
            getPlayingList.mOffset = jsonData.getInt("offset");
            getPlayingList.mLength = jsonData.getInt("length");

            return true;
        } catch (Throwable t)
        {
            mLog.error("[DECODER] " + aDecodingObj.getID(), t);
            return false;
        }
    }

    public Object encode(IResponseMessage aResponseMessage) throws ServerException
    {
        try
        {
            JSONObject encodingObj = new JSONObject();
            // put response data into json object
            encodingObj.put("mid", aResponseMessage.getID());
            // cast response obj
            GetPlayingListResponse getPlayingList = (GetPlayingListResponse) aResponseMessage;
            encodingObj.put("code", getPlayingList.mCode);
            if (getPlayingList.mCode == ResponseCode.FAILURE)
            {
            } else if (getPlayingList.mCode == ResponseCode.SUCCESS)
            {
                encodingObj.put("num_playing_room", getPlayingList.mNumPlayingRoom);
                // room dummy
                JSONArray arrRooms = new JSONArray();
                if (getPlayingList.mPlayingRooms != null)
                {
                    for (RoomEntity roomEntity : getPlayingList.mPlayingRooms)
                    {
                        // with each playing room
                        JSONObject jRoom = new JSONObject();
                        jRoom.put("room_id", roomEntity.mRoomId);
                        jRoom.put("room_name", roomEntity.mRoomName);
                        jRoom.put("playing_size", roomEntity.mPlayingSize);
                        jRoom.put("entering_size", roomEntity.mEnteringSize);
                        jRoom.put("capacity", roomEntity.mCapacity);
                        if(roomEntity.mPassword != null){
                        	jRoom.put("isSecure", true);
                        } else {
                        	jRoom.put("isSecure", false);
                        }
                        // attached object
//                        BacayTable table = (BacayTable) roomEntity.mAttactmentData;
//                        if (table != null)
//                        {
//                        	jRoom.put("minBet", table.getMinBet());
//                        	BacayPlayer roomOwner = table.getRoomOwner();
//                        	jRoom.put("level", roomOwner.level);
//                        	jRoom.put("avatar", roomOwner.avatarID);
//                        	jRoom.put("username", roomOwner.username);
//                        } 
                        // then add to array of rooms
                        arrRooms.put(jRoom);
                    }
                }
                encodingObj.put("playing_rooms", arrRooms);
            }
            // response encoded obj
            return encodingObj;
        } catch (Throwable t)
        {
            mLog.error("[ENCODER] " + aResponseMessage.getID(), t);
            return null;
        }
    }

}
